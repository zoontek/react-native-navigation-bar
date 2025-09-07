import { useEffect, useMemo, useRef } from "react";
import NativeModule from "./specs/NativeNavigationBarModule";
import type { NavigationBarProps, NavigationBarStyle } from "./types";

// Merges the entries stack
function mergeEntriesStack(entriesStack: NavigationBarProps[]) {
  return entriesStack.reduce<{
    barStyle: NavigationBarStyle | undefined;
    hidden: boolean | undefined;
  }>(
    (prev, cur) => {
      for (const prop in cur) {
        if (cur[prop as keyof NavigationBarProps] != null) {
          // @ts-expect-error
          prev[prop] = cur[prop];
        }
      }
      return prev;
    },
    {
      barStyle: undefined,
      hidden: undefined,
    },
  );
}

// Returns an object to insert in the props stack from the props
function createStackEntry({
  barStyle,
  hidden,
}: NavigationBarProps): NavigationBarProps {
  return { barStyle, hidden }; // Create a copy
}

const entriesStack: NavigationBarProps[] = [];

// Timer for updating the native module values at the end of the frame
let updateImmediate: NodeJS.Immediate | null = null;

// The current merged values from the entries stack
const currentValues: {
  barStyle: NavigationBarStyle | undefined;
  hidden: boolean | undefined;
} = {
  barStyle: undefined,
  hidden: undefined,
};

function setStyle(style: NavigationBarStyle | undefined) {
  if (style !== currentValues.barStyle) {
    currentValues.barStyle = style;

    if (NativeModule != null) {
      NativeModule.setStyle(style ?? "default");
    }
  }
}

/**
 * Set the navigation bar style.
 *
 * @param style Navigation bar style to set.
 */
function setBarStyle(style: NavigationBarStyle) {
  setStyle(style);
}

/**
 * Show or hide the navigation bar.
 *
 * @param hidden Hide the navigation bar.
 */
function setHidden(hidden: boolean) {
  if (hidden !== currentValues.hidden) {
    currentValues.hidden = hidden;

    if (NativeModule != null) {
      NativeModule.setHidden(hidden);
    }
  }
}

// Updates the native navigation bar with the entries from the stack
function updateEntriesStack() {
  if (NativeModule != null) {
    if (updateImmediate != null) {
      clearImmediate(updateImmediate);
    }

    updateImmediate = setImmediate(() => {
      const { barStyle, hidden } = mergeEntriesStack(entriesStack);

      setStyle(barStyle);

      if (hidden != null) {
        setHidden(hidden);
      }
    });
  }
}

/**
 * Push a `NavigationBar` entry onto the stack.
 * The return value should be passed to `popStackEntry` when complete.
 *
 * @param props Object containing the `NavigationBar` props to use in the stack entry.
 */
function pushStackEntry(props: NavigationBarProps): NavigationBarProps {
  const entry = createStackEntry(props);
  entriesStack.push(entry);
  updateEntriesStack();
  return entry;
}

/**
 * Remove an existing `NavigationBar` stack entry from the stack.
 *
 * @param entry Entry returned from `pushStackEntry`.
 */
function popStackEntry(entry: NavigationBarProps): void {
  const index = entriesStack.indexOf(entry);
  if (index !== -1) {
    entriesStack.splice(index, 1);
  }
  updateEntriesStack();
}

/**
 * Replace an existing `NavigationBar` stack entry with new props.
 *
 * @param entry Entry returned from `pushStackEntry` to replace.
 * @param props Object containing the `NavigationBar` props to use in the replacement stack entry.
 */
function replaceStackEntry(
  entry: NavigationBarProps,
  props: NavigationBarProps,
): NavigationBarProps {
  const newEntry = createStackEntry(props);
  const index = entriesStack.indexOf(entry);
  if (index !== -1) {
    entriesStack[index] = newEntry;
  }
  updateEntriesStack();
  return newEntry;
}

export function NavigationBar(props: NavigationBarProps) {
  const { barStyle, hidden } = props;

  const stableProps = useMemo<NavigationBarProps>(
    () => ({ barStyle, hidden }),
    [barStyle, hidden],
  );

  const stackEntryRef = useRef<NavigationBarProps | null>(null);

  useEffect(() => {
    // Every time a NavigationBar component is mounted, we push it's prop to a stack
    // and always update the native navigation bar with the props from the top of then
    // stack. This allows having multiple NavigationBar components and the one that is
    // added last or is deeper in the view hierarchy will have priority.
    stackEntryRef.current = pushStackEntry(stableProps);

    return () => {
      // When a NavigationBar is unmounted, remove itself from the stack and update
      // the native bar with the next props.
      if (stackEntryRef.current) {
        popStackEntry(stackEntryRef.current);
      }
    };
  }, []);

  useEffect(() => {
    if (stackEntryRef.current) {
      stackEntryRef.current = replaceStackEntry(
        stackEntryRef.current,
        stableProps,
      );
    }
  }, [stableProps]);

  return null;
}

NavigationBar.pushStackEntry = pushStackEntry;
NavigationBar.popStackEntry = popStackEntry;
NavigationBar.replaceStackEntry = replaceStackEntry;
NavigationBar.setBarStyle = setBarStyle;
NavigationBar.setHidden = setHidden;
