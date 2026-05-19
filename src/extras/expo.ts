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
  const names = new Set([
    "android:enforceNavigationBarContrast",
    "enforceNavigationBarContrastFallback",
  ]);

  return withAndroidStyles(config, (config) => {
    const { android = {} } = props;
    const { enforceNavigationBarContrast = false } = android;

    config.modResults.resources.style = config.modResults.resources.style?.map(
      (style): typeof style => {
        if (style.$.name === "AppTheme") {
          style.item = style.item.filter((item) => !names.has(item.$.name));

          for (const name of names) {
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
