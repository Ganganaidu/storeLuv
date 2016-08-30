package com.thisisswitch.storelove.fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**This is the text view with bold font class 
 * By calling this class font of a "Text View" is modified
 * @author Gangadhar Naidu
 * */

public class Textview_bold extends TextView {

	public Textview_bold(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public Textview_bold(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public Textview_bold(Context context) {
		super(context);
		init();
	}

	/**Font is set based on the activity the user is in
	 * so using try catch block to verify the view and setting the font
	 * by this way font can be easily changed*/
	private void init() {
		if (!isInEditMode()) {

			Typeface myFonts = Typeface.createFromAsset(getContext().getAssets(), "font/Roboto-Medium.ttf");
			setTypeface(myFonts);	
		}
	}
}
