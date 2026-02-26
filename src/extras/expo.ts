import {
  type ConfigPlugin,
  createRunOncePlugin,
  withAndroidStyles,
} from "@expo/config-plugins";

const PACKAGE_NAME = "@zoontek/react-native-navigation-bar";

type NavigationBarPluginConfig = {
  android?: {
    enforceNavigationBarContrast?: boolean;
  };
};

const plugin: ConfigPlugin<NavigationBarPluginConfig | undefined> = (
  config,
  props = {},
) => {
  const name = "enforceNavigationBarContrast";

  return withAndroidStyles(config, (config) => {
    const { android = {} } = props;
    const { enforceNavigationBarContrast } = android;

    config.modResults.resources.style = config.modResults.resources.style?.map(
      (style): typeof style => {
        if (style.$.name === "AppTheme") {
          style.item = style.item.filter((item) => item.$.name !== name);

          if (enforceNavigationBarContrast != null) {
            style.item = style.item.filter(
              (item) => item.$.name !== `android:${name}`,
            );

            style.item.push({
              $: { name },
              _: String(enforceNavigationBarContrast),
            });
          }
        }

        return style;
      },
    );

    return config;
  });
};

export const withNavigationBar = createRunOncePlugin(plugin, PACKAGE_NAME);

export default (
  config: NavigationBarPluginConfig,
): [typeof PACKAGE_NAME, NavigationBarPluginConfig] => [PACKAGE_NAME, config];
