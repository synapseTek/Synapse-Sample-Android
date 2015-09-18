package mindtek.it.synapsesample.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import mindtek.common.ui.images.ImageDownloader;
import mindtek.it.synapsesample.R;


public class GalleryAdapter extends PagerAdapter {
	Context context;
	ArrayList<String> imageURLs;

	public GalleryAdapter(Context context, ArrayList<String> _imageURLs){
		this.context=context;
		this.imageURLs = _imageURLs;
	}
	@Override
	public int getCount() {
		return imageURLs.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((RelativeLayout) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.gallery_image, null);

		ImageView imageView = (ImageView) layout.findViewById(R.id.img);
		ImageDownloader.loadImage(context, imageView, imageURLs.get(position), R.drawable.default_bigimage);
		((ViewPager) container).addView(layout, 0);
		
		return layout;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((RelativeLayout) object);
	}
}