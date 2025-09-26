import { ConfigPlugin, withInfoPlist } from 'expo/config-plugins';

type IosProps = {
    message?: string;
};

const withIosPlugin: ConfigPlugin<IosProps> = (config, options = {}) => {
    const message = options.message || 'Hello world, from Expo plugin!';

    return withInfoPlist(config, config => {
        config.modResults.HelloWorldMessage = message;
        return config;
    });
};

export default withIosPlugin;
