import { ConfigPlugin, withInfoPlist } from 'expo/config-plugins';

const withIosPlugin: ConfigPlugin = config => {
    const message = 'Hello world, from Expo plugin!';

    return withInfoPlist(config, config => {
        config.modResults.HelloWorldMessage = message;
        return config;
    });
};

export default withIosPlugin;
