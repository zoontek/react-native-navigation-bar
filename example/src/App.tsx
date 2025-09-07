import SegmentedControl from "@react-native-segmented-control/segmented-control";
import {
  NavigationBar,
  type NavigationBarStyle,
} from "@zoontek/react-native-navigation-bar";
import { useEffect, useState, type ReactNode } from "react";
import {
  Appearance,
  Text as BaseText,
  StatusBar,
  StyleSheet,
  Switch,
  useColorScheme,
  View,
  type StatusBarStyle,
  type TextProps,
} from "react-native";

const DARK_BACKGROUND = "#1F2937";
const DARK_TEXT = "#374151";
const LIGHT_BACKGROUND = "#F9FAFB";
const LIGHT_TEXT = "#E5E7EB";

const styles = StyleSheet.create({
  container: {
    backgroundColor: LIGHT_BACKGROUND,
    flex: 1,
    justifyContent: "center",
    paddingHorizontal: 16,
  },
  darkContainer: {
    backgroundColor: DARK_BACKGROUND,
  },
  row: {
    flexDirection: "row",
    alignItems: "center",
  },
});

const Space = ({ size }: { size: number }) => (
  <View
    accessibilityRole="none"
    collapsable={true}
    style={{ height: size, width: size }}
  />
);

const Title = ({ children }: { children: ReactNode }) => (
  <>
    <Text style={{ fontSize: 20, fontWeight: "700" }}>{children}</Text>
    <Space size={16} />
  </>
);

const Text = ({ style, ...props }: TextProps) => {
  const dark = useColorScheme() === "dark";

  return (
    <BaseText
      style={[{ color: dark ? LIGHT_TEXT : DARK_TEXT }, style]}
      {...props}
    />
  );
};

const SCHEMES = ["system", "light", "dark"];

const STYLES: NavigationBarStyle[] = [
  "default",
  "light-content",
  "dark-content",
];

export const App = () => {
  const dark = useColorScheme() === "dark";

  const thumbColor = dark ? LIGHT_TEXT : "#fff";
  const trackColor = dark
    ? { false: "#1c1c1f", true: "#2b3e55" }
    : { false: "#eeeef0", true: "#ccd8e5" };

  const [schemeIndex, setSchemeIndex] = useState(0);
  const [statusBarStyleIndex, setStatusBarStyleIndex] = useState(0);
  const [navigationBarStyleIndex, setNavigationBarStyleIndex] = useState(0);
  const [statusBarHidden, setStatusBarHidden] = useState(false);
  const [navigationBarHidden, setNavigationBarHidden] = useState(false);

  useEffect(() => {
    const value = SCHEMES[schemeIndex];
    const scheme = value === "light" || value === "dark" ? value : null;
    Appearance.setColorScheme(scheme);
  }, [schemeIndex]);

  return (
    <View style={[styles.container, dark && styles.darkContainer]}>
      <StatusBar
        barStyle={STYLES[statusBarStyleIndex]}
        hidden={statusBarHidden}
      />

      <NavigationBar
        barStyle={STYLES[navigationBarStyleIndex]}
        hidden={navigationBarHidden}
      />

      <Title>Theme</Title>

      <SegmentedControl
        appearance={dark ? "dark" : "light"}
        values={SCHEMES}
        selectedIndex={schemeIndex}
        onValueChange={(value) => {
          setSchemeIndex(SCHEMES.indexOf(value));
        }}
      />

      <Space size={32} />

      <View style={[styles.row, { justifyContent: "space-between" }]}>
        <Title>{"<StatusBar />"}</Title>

        <View style={styles.row}>
          <Text>Hide</Text>
          <Space size={4} />

          <Switch
            thumbColor={thumbColor}
            trackColor={trackColor}
            value={statusBarHidden}
            onValueChange={setStatusBarHidden}
          />
        </View>
      </View>

      <Space size={16} />

      <SegmentedControl
        appearance={dark ? "dark" : "light"}
        values={STYLES}
        selectedIndex={statusBarStyleIndex}
        onValueChange={(value) => {
          setStatusBarStyleIndex(STYLES.indexOf(value as StatusBarStyle));
        }}
      />

      <Space size={32} />

      <View style={[styles.row, { justifyContent: "space-between" }]}>
        <Title>{"<NavigationBar />"}</Title>

        <View style={styles.row}>
          <Text>Hide</Text>
          <Space size={4} />

          <Switch
            thumbColor={thumbColor}
            trackColor={trackColor}
            value={navigationBarHidden}
            onValueChange={setNavigationBarHidden}
          />
        </View>
      </View>

      <Space size={16} />

      <SegmentedControl
        appearance={dark ? "dark" : "light"}
        values={STYLES}
        selectedIndex={navigationBarStyleIndex}
        onValueChange={(value) => {
          setNavigationBarStyleIndex(
            STYLES.indexOf(value as NavigationBarStyle),
          );
        }}
      />
    </View>
  );
};
