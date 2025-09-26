import { ConfigPlugin, withAndroidManifest } from 'expo/config-plugins';

type AndroidProps = {
    message?: string;
};

const withAndroidPlugin: ConfigPlugin<AndroidProps> = (config, options = {}) => {
    const message = options.message || 'Hello world, from Expo plugin!';

    return withAndroidManifest(config, config => {
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
};

export default withAndroidPlugin;
