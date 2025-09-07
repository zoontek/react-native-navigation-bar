import {
  type ConfigPlugin,
  createRunOncePlugin,
  withAndroidStyles,
} from "@expo/config-plugins";

type AndroidProps = {
  enforceNavigationBarContrast?: boolean;
};

type Props = { android?: AndroidProps } | undefined;

const withAndroidNavigationBarStyles: ConfigPlugin<Props> = (
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

export default createRunOncePlugin(
  withAndroidNavigationBarStyles,
  "zoontek/react-native-navigation-bar",
);
