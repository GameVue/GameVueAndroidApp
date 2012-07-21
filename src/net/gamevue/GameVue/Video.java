package net.gamevue.GameVue;

import android.graphics.Bitmap;

public class Video
{
	public int		id,
					thumb;
	public String	md5,
					name;
	public Bitmap	image;
	
	public String getImageLocation(){
		return "uploads/thumbs/" + md5 + "md_" + thumb + ".png";
	}
}
