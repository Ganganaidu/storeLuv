package com.thisisswitch.storelove.fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

/**This is the Button with bold font class 
 * By calling this class font of a "Button" is modified
 * @author Gangadhar Naidu
 * */

public class Button_bold extends Button {

	public Button_bold(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public Button_bold(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public Button_bold(Context context) {
		super(context);
		init();
	}

	/**Font is set based on the activity the user is in
	 * so using try catch block to verify the view and setting the font
	 * by this way font can be easily changed*/
	private void init() {
		if (!isInEditMode()) {
			//Typeface myFonts = Typeface.createFromAsset(getContext().getAssets(), "font/helvetica_bold.otf");
			Typeface myFonts = Typeface.createFromAsset(getContext().getAssets(), "font/Roboto-Medium.ttf");
			setTypeface(myFonts);	
		}
	}
}
