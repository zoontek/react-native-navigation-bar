package com.zoontek.rnnavigationbar

import com.facebook.react.BaseReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.model.ReactModuleInfo
import com.facebook.react.module.model.ReactModuleInfoProvider

class NavigationBarPackage : BaseReactPackage() {
  override fun getModule(name: String, reactContext: ReactApplicationContext): NativeModule? {
    return when (name) {
      NativeNavigationBarModuleSpec.NAME -> NavigationBarModule(reactContext)
      else -> null
    }
  }

  override fun getReactModuleInfoProvider(): ReactModuleInfoProvider {
    return ReactModuleInfoProvider {
      val moduleInfo = ReactModuleInfo(
        NativeNavigationBarModuleSpec.NAME,
        NativeNavigationBarModuleSpec.NAME,
        canOverrideExistingModule = false,
        needsEagerInit = true,
        isCxxModule = false,
        isTurboModule = true,
      )

      mapOf(
        NativeNavigationBarModuleSpec.NAME to moduleInfo
      )
    }
  }
}
