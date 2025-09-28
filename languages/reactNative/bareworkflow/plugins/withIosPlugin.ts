import {ConfigPlugin, IOSConfig, withInfoPlist, withXcodeProject} from 'expo/config-plugins';

type IosProps = {
    message?: string;
};

const withIosPlugin: ConfigPlugin<IosProps> = (config, options = {}) => {
    const message = options.message || 'Hello world, from Expo plugin!';
    // ok this is nice! Can add modifiers to each config available on expo/config-plugins
    // https://docs.expo.dev/guides/config-plugins/
    // Must be applied sequentially, so we return the config from the previous plugin
    // and pass to the second plugin

    config = withInfoPlist(config, config => {
        config.modResults.HelloWorldMessage = message;
        return config;
    });
    config = withXcodeProject(config, async (config) => {
        config.modResults = IOSConfig.Name.setProductName({name: "Not the same on app settings"}, config.modResults);
        return config;
    });
    return config;
};

export default withIosPlugin;
