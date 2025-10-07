import { ConfigPlugin } from 'expo/config-plugins';
import withAndroidMainPlugin from './withAndroidMain';

const withPlugin: ConfigPlugin = config => {
    // Apply Android modifications first
    config = withAndroidMainPlugin(config);
    return config;
};

export default withPlugin;