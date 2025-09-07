# @zoontek/react-native-navigation-bar

React Native `StatusBar` long-lost twin. A component to control your Android app's navigation bar.

[![mit licence](https://img.shields.io/dub/l/vibe-d.svg?style=for-the-badge)](https://github.com/zoontek/react-native-navigation-bar/blob/main/LICENSE)
[![npm version](https://img.shields.io/npm/v/@zoontek/react-native-navigation-bar.svg?style=for-the-badge)](https://www.npmjs.com/package/@zoontek/react-native-navigation-bar)
[![npm downloads](https://img.shields.io/npm/dt/@zoontek/react-native-navigation-bar.svg?label=downloads&style=for-the-badge)](https://www.npmjs.com/package/@zoontek/react-native-navigation-bar)

## Support

This library follows the React Native [releases support policy](https://github.com/reactwg/react-native-releases/blob/main/docs/support.md).<br>
It is supporting the **latest version**, and the **two previous minor series**.

## Installation

```bash
$ npm i -S react-native-edge-to-edge
# --- or ---
$ yarn add react-native-edge-to-edge
```

## API

### NavigationBar

TODO

```tsx
import { NavigationBar } from "@zoontek/react-native-navigation-bare";

type NavigationBarStyle = "default" | "light-content" | "dark-content";

type NavigationBarProps = {
  // set the color of the navigation bar content
  barStyle?: NavigationBarStyle;
  // hide the navigation bar (has no effect on iOS)
  hidden?: boolean;
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

#### NavigationBar.setStyle

Set the navigation bar style.

```ts
NavigationBar.setStyle(style /*: NavigationBarStyle */);
```

#### NavigationBar.setHidden

Show or hide the navigation bar.

```ts
NavigationBar.setHidden(style /*: boolean */);
```

## Troubleshooting 🤔

#### The navigation bar style is erratic

There's currently [an open issue](https://issuetracker.google.com/issues/346386744) with the Android 15 emulator image regarding the navigation bar style when it is is fully transparent. This issue does not occur on physical devices.
