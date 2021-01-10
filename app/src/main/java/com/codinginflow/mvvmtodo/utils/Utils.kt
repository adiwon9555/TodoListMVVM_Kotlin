package com.codinginflow.mvvmtodo.utils

//This extension function is basically to convert a statement to an expression (Specially for when) -> For compile timesafety
val <T> T.exhaustive : T
    get() = this
