import {ConfigPlugin, withMainApplication} from 'expo/config-plugins';
import {addImports, appendContentsInsideDeclarationBlock} from "expo-web-browser/plugin/src/utils";
import {ExpoConfig} from "@expo/config-types";

function addMainApplicationMod(contents: string) {
    if (contents.includes('private val runningActivities = ArrayList<Class<*>>()')) {
        return contents;
    }

    const codeMod = `
  private val runningActivities = ArrayList<Class<*>>()

  private val lifecycleCallbacks = object : ActivityLifecycleCallbacks {
    override fun onActivityCreated(activity: Activity, p1: Bundle?) {
      if (!runningActivities.contains(activity::class.java)) runningActivities.add(activity::class.java)
    }

    override fun onActivityStarted(p0: Activity) = Unit
    override fun onActivityResumed(p0: Activity) = Unit
    override fun onActivityPaused(p0: Activity) = Unit
    override fun onActivityStopped(p0: Activity) = Unit
    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) = Unit

    override fun onActivityDestroyed(activity: Activity) {
      if (runningActivities.contains(activity::class.java)) runningActivities.remove(activity::class.java)
    }
  }
  
  fun isActivityInBackStack(cls: Class<*>?) = runningActivities.contains(cls)

  override fun onTerminate() {
    super.onTerminate()
    unregisterActivityLifecycleCallbacks(lifecycleCallbacks)
  }
  `;

    return appendContentsInsideDeclarationBlock(contents, 'class MainApplication', codeMod);
}

function modifyMainApplication(config: ExpoConfig) {
    return withMainApplication(config, (config) => {
        const mainApplication = config.modResults;

        const importsMod = addImports(
            mainApplication.contents,
            ['android.app.Activity', 'android.os.Bundle'],
            false
        );

        let contents = importsMod;
        if (
            !mainApplication.contents.includes('registerActivityLifecycleCallbacks(lifecycleCallbacks)')
        ) {
            contents = appendContentsInsideDeclarationBlock(
                importsMod,
                'onCreate',
                'registerActivityLifecycleCallbacks(lifecycleCallbacks)'
            );
        }

        const result = addMainApplicationMod(contents);

        return {
            ...config,
            modResults: {
                ...config.modResults,
                contents: result,
            },
        };
    });
}

const withAndroidMainPlugin: ConfigPlugin = config => {
    config = modifyMainApplication(config);
    return config;
};

export default withAndroidMainPlugin;