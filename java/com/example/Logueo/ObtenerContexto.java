package com.example.Logueo;

import android.app.Application;
import android.content.Context;

public class ObtenerContexto extends Application {
        private static Application instance;

        @Override
        public void onCreate() {
            super.onCreate();
            instance = this;
        }

        public static Context getContext() {
            return instance.getApplicationContext();
        }
}