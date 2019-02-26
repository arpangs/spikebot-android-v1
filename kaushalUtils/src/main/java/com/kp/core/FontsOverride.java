package com.kp.core;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;

public class FontsOverride {
	public static void setDefaultFont(Context context,
			String staticTypefaceFieldName, String fontAssetName) {
		final Typeface regular = Typeface.createFromAsset(context.getAssets(),
				fontAssetName);
		replaceFont(staticTypefaceFieldName, regular);
	}

	protected static void replaceFont(String staticTypefaceFieldName,
			final Typeface newTypeface) {
		try {
			final Field staticField = Typeface.class
					.getDeclaredField(staticTypefaceFieldName);
			staticField.setAccessible(true);
			staticField.set(null, newTypeface);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	private static final int BOLD = 1;
    private static final int BOLD_ITALIC = 2;
    private static final int ITALIC = 3;
    private static final int LIGHT = 4;
    private static final int CONDENSED = 5;
    private static final int THIN = 6;
    private static final int MEDIUM = 7;
    private static final int REGULAR = 8;

    private Context context;

    public FontsOverride(Context context) {
        this.context = context;
    }

    public void loadFonts() {
        Map<String, Typeface> fontsMap = new HashMap<>();
        fontsMap.put("sans-serif", getTypeface("SanFranciscoText-Regular.ttf", REGULAR));
        fontsMap.put("sans-serif-bold", getTypeface("SanFranciscoText-Bold.ttf", BOLD));
//        fontsMap.put("sans-serif-italic", getTypeface("comic.ttf", ITALIC));
//        fontsMap.put("sans-serif-light", getTypeface("SanFranciscoText-Light.ttf", LIGHT));
//        fontsMap.put("sans-serif-condensed", getTypeface("comic.ttf", CONDENSED));
        fontsMap.put("sans-serif-thin", getTypeface("SanFranciscoText-Semibold.ttf", THIN));
        fontsMap.put("sans-serif-medium", getTypeface("SanFranciscoText-Medium.ttf", MEDIUM));
        overrideFonts(fontsMap);
    }

    private void overrideFonts(Map<String, Typeface> typefaces) {
        if (Build.VERSION.SDK_INT == 21) {
            try {
                final Field field = Typeface.class.getDeclaredField("sSystemFontMap");
                field.setAccessible(true);
                Map<String, Typeface> oldFonts = (Map<String, Typeface>) field.get(null);
                if (oldFonts != null) {
                    oldFonts.putAll(typefaces);
                } else {
                    oldFonts = typefaces;
                }
                field.set(null, oldFonts);
                field.setAccessible(false);
            } catch (Exception e) {
            }
        } else {
            try {
                for (Map.Entry<String, Typeface> entry : typefaces.entrySet()) {
                    final Field staticField = Typeface.class.getDeclaredField(entry.getKey());
                    staticField.setAccessible(true);
                    staticField.set(null, entry.getValue());
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private Typeface getTypeface(String fontFileName, int fontType) {
        final Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/" + fontFileName);
        return Typeface.create(tf, fontType);
    }
}
