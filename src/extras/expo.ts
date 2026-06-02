import {
  type ConfigPlugin,
  createRunOncePlugin,
  withAndroidStyles,
} from "@expo/config-plugins";

const PACKAGE_NAME = "@zoontek/react-native-navigation-bar";

type NavigationBarPluginConfig = {
  enforceContrast: boolean;
};

const plugin: ConfigPlugin<NavigationBarPluginConfig | undefined> = (
  config,
  props = { enforceContrast: false },
) => {
  const androidAttrName = "android:enforceNavigationBarContrast";
  const libraryAttrName = "enforceNavigationBarContrast";

  const names = new Set([androidAttrName, libraryAttrName]);

  return withAndroidStyles(config, (config) => {
    const { enforceContrast = false } = props;
    const _ = String(enforceContrast);

    config.modResults.resources.style = config.modResults.resources.style?.map(
      (style): typeof style => {
        if (style.$.name === "AppTheme") {
          style.item = style.item.filter((item) => !names.has(item.$.name));

          style.item.push(
            { $: { name: androidAttrName, "tools:targetApi": "29" }, _ },
            { $: { name: libraryAttrName }, _ },
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
