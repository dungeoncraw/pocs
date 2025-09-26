import { ConfigPlugin } from 'expo/config-plugins';
import withAndroidPlugin from './withAndroidPlugin';
import withIosPlugin from './withIosPlugin';

const withPlugin: ConfigPlugin<{message?: string}> = (config, options = {}) => {
    config = withAndroidPlugin(config, options);
    return withIosPlugin(config, options);
};

export default withPlugin;
