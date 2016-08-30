package com.thisisswitch.storelove.fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

/**This is the Edit text font class 
 * By calling this class font of a "Edit text" is modified
 * @author Gangadhar Naidu
 * */

public class EditText_light extends EditText{

	public EditText_light(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public EditText_light(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public EditText_light(Context context) {
		super(context);
		init();
	}

	/**Font is set based on the activity the user is in
	 * so using try catch block to verify the view and setting the font
	 * by this way font can be easily changed*/
	private void init() {
		if (!isInEditMode()) {
			//as the edit text is in edit mode so writing it in else block
		}
		else{
			Typeface myFonts = Typeface.createFromAsset(getContext().getAssets(), "font/Signika-Light.TTF");
			setTypeface(myFonts);	

		}
	}

}
