import {ConfigPlugin, withAndroidManifest, withMainActivity} from 'expo/config-plugins';
import {findNewInstanceCodeBlock} from "@expo/config-plugins/build/android/codeMod";
import {addImports, appendContentsInsideDeclarationBlock} from "expo-web-browser/plugin/src/utils";
import {replaceContentsWithOffset} from "@expo/config-plugins/build/utils/commonCodeMod";

type AndroidProps = {
    message?: string;
};

const withAndroidModulesMainActivity: ConfigPlugin = (config) => {
    return withMainActivity(config, (config) => {
        config.modResults.contents = setModulesMainActivity(
            config.modResults.contents,
            config.modResults.language
        );
        return config;
    });
};
const setModulesMainActivity = (mainActivity: string, language: 'java' | 'kt'): string =>  {
    const isJava = language === 'java';

    if (mainActivity.match(/\s+ReactActivityDelegateWrapper\(/m) != null) {
        return mainActivity;
    }

    if (mainActivity.match(/\s+createReactActivityDelegate\(\)/m) == null) {
        mainActivity = addImports(
            mainActivity,
            ['com.facebook.react.ReactActivityDelegate', 'expo.modules.ReactActivityDelegateWrapper'],
            isJava
        );

        const addReactActivityDelegateBlock = isJava
            ? [
                '\n  @Override',
                '  protected ReactActivityDelegate createReactActivityDelegate() {',
                '    return new ReactActivityDelegateWrapper(this, BuildConfig.IS_NEW_ARCHITECTURE_ENABLED,',
                '      new ReactActivityDelegate(this, getMainComponentName())',
                '    );',
                '  }\n',
            ]
            : [
                '\n  override fun createReactActivityDelegate(): ReactActivityDelegate {',
                '    return ReactActivityDelegateWrapper(this, BuildConfig.IS_NEW_ARCHITECTURE_ENABLED,',
                '      ReactActivityDelegate(this, getMainComponentName())',
                '    );',
                '  }\n',
            ];

        mainActivity = appendContentsInsideDeclarationBlock(
            mainActivity,
            'class MainActivity',
            addReactActivityDelegateBlock.join('\n')
        );
    } else if (mainActivity.match(/\bDefaultReactActivityDelegate\b/g)) {
        mainActivity = addImports(mainActivity, ['expo.modules.ReactActivityDelegateWrapper'], isJava);

        const newInstanceCodeBlock = findNewInstanceCodeBlock(
            mainActivity,
            'DefaultReactActivityDelegate',
            language
        );
        if (newInstanceCodeBlock == null) {
            throw new Error('Unable to find DefaultReactActivityDelegate new instance code block.');
        }

        const replacement = isJava
            ? `new ReactActivityDelegateWrapper(this, BuildConfig.IS_NEW_ARCHITECTURE_ENABLED, ${newInstanceCodeBlock.code})`
            : `ReactActivityDelegateWrapper(this, BuildConfig.IS_NEW_ARCHITECTURE_ENABLED, ${newInstanceCodeBlock.code})`;
        mainActivity = replaceContentsWithOffset(
            mainActivity,
            replacement,
            newInstanceCodeBlock.start,
            newInstanceCodeBlock.end
        );

        return mainActivity;
    } else if (
        mainActivity.match(/\s+MainActivityDelegate\s+extends\s+ReactActivityDelegate\s+\{/) != null ||
        mainActivity.match(/\s+MainActivityDelegate\(.+\)\s+:\s+ReactActivityDelegate.+\{/) != null
    ) {
        mainActivity = addImports(mainActivity, ['expo.modules.ReactActivityDelegateWrapper'], isJava);

        const newInstanceCodeBlock = findNewInstanceCodeBlock(
            mainActivity,
            'MainActivityDelegate',
            language
        );
        if (newInstanceCodeBlock == null) {
            throw new Error('Unable to find MainActivityDelegate new instance code block.');
        }

        const replacement = isJava
            ? `new ReactActivityDelegateWrapper(this, BuildConfig.IS_NEW_ARCHITECTURE_ENABLED, ${newInstanceCodeBlock.code})`
            : `ReactActivityDelegateWrapper(this, BuildConfig.IS_NEW_ARCHITECTURE_ENABLED, ${newInstanceCodeBlock.code})`;
        mainActivity = replaceContentsWithOffset(
            mainActivity,
            replacement,
            newInstanceCodeBlock.start,
            newInstanceCodeBlock.end
        );

        return mainActivity;
    } else {
        mainActivity = addImports(mainActivity, ['expo.modules.ReactActivityDelegateWrapper'], isJava);

        const newInstanceCodeBlock = findNewInstanceCodeBlock(
            mainActivity,
            'ReactActivityDelegate',
            language
        );
        if (newInstanceCodeBlock == null) {
            throw new Error('Unable to find ReactActivityDelegate new instance code block.');
        }

        const replacement = isJava
            ? `new ReactActivityDelegateWrapper(this, ${newInstanceCodeBlock.code})`
            : `ReactActivityDelegateWrapper(this, ${newInstanceCodeBlock.code})`;
        mainActivity = replaceContentsWithOffset(
            mainActivity,
            replacement,
            newInstanceCodeBlock.start,
            newInstanceCodeBlock.end
        );

        return mainActivity;
    }

    return mainActivity;
};
const withAndroidPlugin: ConfigPlugin<AndroidProps> = (config, options = {}) => {
    const message = options.message || 'Hello world, from Expo plugin!';
    config = withAndroidManifest(config, config => {
        const mainApplication = config?.modResults?.manifest?.application?.[0];

        if (mainApplication) {
            if (!mainApplication['meta-data']) {
                mainApplication['meta-data'] = [];
            }

            mainApplication['meta-data'].push({
                $: {
                    'android:name': 'HelloWorldMessage',
                    'android:value': message,
                },
            });
        }
        return config;
    });
    return withAndroidModulesMainActivity(config);
};

export default withAndroidPlugin;
