import type { TurboModule } from "react-native";
import { Platform, TurboModuleRegistry } from "react-native";

interface Spec extends TurboModule {
  setStyle(style: string): void;
  setHidden(hidden: boolean): void;
}

export default Platform.OS === "android"
  ? TurboModuleRegistry.getEnforcing<Spec>("RNNavigationBar")
  : null;
