# @zoontek/react-native-navigation-bar

React Native [`StatusBar`](https://reactnative.dev/docs/statusbar) long-lost twin: A component to control your Android app's navigation bar.

[![mit licence](https://img.shields.io/dub/l/vibe-d?style=for-the-badge)](https://github.com/zoontek/react-native-navigation-bar/blob/main/LICENSE)
[![npm version](https://img.shields.io/npm/v/@zoontek/react-native-navigation-bar?style=for-the-badge)](https://www.npmjs.com/package/@zoontek/react-native-navigation-bar)
[![npm downloads](https://img.shields.io/npm/dt/@zoontek/react-native-navigation-bar?label=downloads&style=for-the-badge)](https://www.npmjs.com/package/@zoontek/react-native-navigation-bar)

<img width="210" src="./docs/logo.svg" alt="Logo">

## Credits

This project has been built and is maintained thanks to the support from [Expo](https://expo.dev).

<a href="https://expo.io">
  <img width="180" src="./docs/expo.svg" alt="Expo">
</a>

## Installation

```bash
$ npm i -S @zoontek/react-native-navigation-bar
# --- or ---
$ yarn add @zoontek/react-native-navigation-bar
```

> [!IMPORTANT]
> This library requires React Native 0.81+ or Expo 54+ with edge-to-edge enabled. To turn it on, set `edgeToEdgeEnabled` to `true` in your project's `gradle.properties` file (this step is not required for Expo, as it is enabled by default).

```ruby
edgeToEdgeEnabled=true # 👈 set this to true
```

## Considerations

### Transparency

Compared to [`react-native-edge-to-edge`](https://github.com/zoontek/react-native-edge-to-edge), this library adopts React Native [`StatusBar`](https://reactnative.dev/docs/statusbar) API and its defaults: the navigation bar is transparent with a `light-content` bar style. To enforce a contrasting (semi-opaque) button navigation bar, set the `enforceNavigationBarContrast` option to `true`.

#### React Native

Edit your `android/app/src/main/res/values/styles.xml` file to inherit from one of the provided themes:

```xml
<resources>
  <style name="AppTheme" parent="Theme.AppCompat.DayNight.NoActionBar">
    <!-- … -->
    <!-- enforce a contrasting navigation bar background (optional) -->
    <item name="enforceNavigationBarContrast">true</item>
  </style>
</resources>
```

#### Expo

Add the library plugin to your `app.json` configuration file and [create a new build](https://docs.expo.dev/develop/development-builds/create-a-build) 👷:

```json
{
  "expo": {
    "plugins": [
      [
        "@zoontek/react-native-navigation-bar",
        { "android": { "enforceNavigationBarContrast": true } }
      ]
    ]
  }
}
```

## API

### NavigationBar

A React component to control the Android button navigation bar (with back / home / recents buttons).

> [!NOTE]
> This component has no effect on other platforms or when gesture navigation is used.

```tsx
import { NavigationBar } from "@zoontek/react-native-navigation-bar";

type NavigationBarStyle = "default" | "light-content" | "dark-content";

type NavigationBarProps = {
  barStyle?: NavigationBarStyle; // set the color of the navigation bar content
  hidden?: boolean; // hide the navigation bar
};

const App = () => (
  <>
    <NavigationBar barStyle="light-content" />
    {/* … */}
  </>
);
```

#### NavigationBar.pushStackEntry

Push a `NavigationBar` entry onto the stack. The return value should be passed to `popStackEntry` when complete.

```ts
const entry: NavigationBarProps = NavigationBar.pushStackEntry(
  props /*: NavigationBarProps */,
);
```

#### NavigationBar.popStackEntry

Remove an existing `NavigationBar` stack entry from the stack.

```ts
NavigationBar.popStackEntry(entry /*: NavigationBarProps */);
```

#### NavigationBar.replaceStackEntry

Replace an existing `NavigationBar` stack entry with new props.

```ts
const entry: NavigationBarProps = NavigationBar.replaceStackEntry(
  entry /*: NavigationBarProps */,
  props /*: NavigationBarProps */,
);
```

#### NavigationBar.setBarStyle

Set the navigation bar style.

```ts
NavigationBar.setBarStyle(style /*: NavigationBarStyle */);
```

#### NavigationBar.setHidden

Show or hide the navigation bar.

```ts
NavigationBar.setHidden(style /*: boolean */);
```

## Troubleshooting 🤔

#### The bar style blinks at app start

Since the navigation bar is set at runtime rather than through themes, you may notice a brief style change at startup. This can be avoided by [adding a splash screen](https://github.com/zoontek/react-native-bootsplash).

#### The bar style behavior is erratic

There's currently [an open issue](https://issuetracker.google.com/issues/346386744) with the Android 15 emulator image regarding the navigation bar style when it is fully transparent. This issue does not occur on physical devices.
