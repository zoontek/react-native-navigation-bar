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
  const androidAttributeName = "android:enforceNavigationBarContrast";
  const libraryAttributeName = "enforceNavigationBarContrast";
  const attributeNames = new Set([androidAttributeName, libraryAttributeName]);

  return withAndroidStyles(config, (config) => {
    const { android = {} } = props;
    const { enforceNavigationBarContrast = false } = android;
    const _ = String(enforceNavigationBarContrast);

    config.modResults.resources.style = config.modResults.resources.style?.map(
      (style): typeof style => {
        if (style.$.name === "AppTheme") {
          style.item = style.item.filter(
            (item) => !attributeNames.has(item.$.name),
          );

          style.item.push(
            { $: { name: androidAttributeName, "tools:targetApi": "29" }, _ },
            { $: { name: libraryAttributeName }, _ },
          );
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
