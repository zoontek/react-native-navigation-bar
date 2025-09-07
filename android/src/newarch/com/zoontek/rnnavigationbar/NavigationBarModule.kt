package com.zoontek.rnnavigationbar

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.annotations.ReactModule

@ReactModule(name = NavigationBarModuleImpl.NAME)
class NavigationBarModule(reactContext: ReactApplicationContext) :
  NativeNavigationBarModuleSpec(reactContext) {

  init {
    NavigationBarModuleImpl.initialize(reactApplicationContext)
  }

  override fun getName(): String {
    return NavigationBarModuleImpl.NAME
  }

  override fun setStyle(style: String) {
    NavigationBarModuleImpl.setStyle(reactApplicationContext, style)
  }

  override fun setHidden(hidden: Boolean) {
    NavigationBarModuleImpl.setHidden(reactApplicationContext, hidden)
  }
}
