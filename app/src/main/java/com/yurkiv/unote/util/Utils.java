/*******************************************************************************
 * Copyright 2013 Gabriele Mariotti
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.yurkiv.unote.util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Point;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import com.yurkiv.unote.R;

/**
 * 
 * @author Gabriele Mariotti (gabri.mariotti@gmail.com)
 *
 */
public class Utils {

	
	/**
	 * Utility class for colors
	 * 
	 * @author Gabriele Mariotti (gabri.mariotti@gmail.com)
	 *
	 */
	public static class ColorUtils{
		
		/**
		 * Create an array of int with colors
		 * 
		 * @param context
		 * @return
		 */
		public static int[] colorChoice(Context context){
		
			int[] mColorChoices=null;	
			String[] color_array = context.getResources().getStringArray(R.array.default_color_choice_values);
		
		    if (color_array!=null && color_array.length>0) {
		        mColorChoices = new int[color_array.length];
		        for (int i = 0; i < color_array.length; i++) {
		            mColorChoices[i] = Color.parseColor(color_array[i]);
		        }
		    }
		    return mColorChoices;
		}
		
		/**
		 * Parse whiteColor
		 * 
		 * @return
		 */
		public static int parseWhiteColor(){
			return Color.parseColor("#FFFFFF");
		}
		
	}
		
	public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

	private static int screenWidth = 0;
	private static int screenHeight = 0;


	public static int dpToPx(int dp) {
		return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
	}


	public static int getScreenHeight(Context c) {
		if (screenHeight == 0) {
			WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
			Display display = wm.getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			screenHeight = size.y;
		}


		return screenHeight;
	}


	public static int getScreenWidth(Context c) {
		if (screenWidth == 0) {
			WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
			Display display = wm.getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			screenWidth = size.x;
		}


		return screenWidth;
	}

	public static int getActionBarSize(Context context) {
		TypedValue typedValue = new TypedValue();
		int[] textSizeAttr = new int[]{R.attr.actionBarSize};
		int indexOfAttrTextSize = 0;
		TypedArray a = context.obtainStyledAttributes(typedValue.data, textSizeAttr);
		int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
		a.recycle();
		int systemBarSize=context.getResources().getDimensionPixelSize(R.dimen.tool_bar_top_padding);
		return actionBarSize+systemBarSize;
	}

}
