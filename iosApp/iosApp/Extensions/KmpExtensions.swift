//
//  KmpExtensions.swift
//  iosApp
//
//  Created by Owen LeJeune on 2026-01-24.
//

import Shared

extension Int64 {
    var asKotlinLong: KotlinLong {
        return KotlinLong(value: self)
    }
}

extension Int32 {
    var asKotlinInt: KotlinInt {
        return KotlinInt(int: self)
    }
}

extension Bool {
    var asKotlinBool: KotlinBoolean {
        return KotlinBoolean(bool: self)
    }
}
