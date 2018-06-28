package com.xmpp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.android.cync.R;
import com.cync.model.ChatMessage;

import java.util.ArrayList;

/**
 * AwesomeAdapter is a Custom class to implement custom row in ListView
 * 
 *
 * 
 */
public class ChatAdapterNew extends BaseAdapter
{
	private Context mContext;
	private ArrayList<ChatMessage> mMessages;

	public ChatAdapterNew(Context context, ArrayList<ChatMessage> messages) {
		super();
		this.mContext = context;
		this.mMessages = messages;

	}

	@Override
	public int getCount() {
		return mMessages.size();
	}

	@Override
	public Object getItem(int position) {
		return mMessages.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ChatMessage message = (ChatMessage) this.getItem(position);

		ViewHolder holder;
		if (convertView == null)
		{
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.chat_row, parent, false);
			holder.messageMine = (TextView) convertView
					.findViewById(R.id.message_text_Mine);
			holder.messageOther = (TextView) convertView
					.findViewById(R.id.message_text_other);
			holder.chattimeMine = (TextView) convertView
					.findViewById(R.id.chattime_mine);
			holder.chattimeOther = (TextView) convertView
					.findViewById(R.id.chattime_other);
			holder.rlMine = (RelativeLayout) convertView
					.findViewById(R.id.rlMine);
			holder.rlOther = (LinearLayout) convertView
					.findViewById(R.id.rlOther);
			 holder.ProfileMine = (ImageView)
			 convertView.findViewById(R.id.ProfileMine);
			 holder.ProfileOther= (ImageView)
			 convertView.findViewById(R.id.ProfileOther);

			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();
		
		String time[] = message.getTimestamp().split("\\s+");

		holder.messageMine.setText(message.getMessage());
		holder.messageOther.setText(message.getMessage());
		holder.chattimeMine.setText(time[0]+"   "+time[1]);
		holder.chattimeOther.setText(time[0]+"   "+time[1]);
//		imageLoader.displayImage(Constants.baseUrl
//				+ message.get(position).getUser_pic(), holder.ProfileMine,
//				optionsProfile, new SimpleImageLoadingListener() {
//					@Override
//					public void onLoadingStarted(String imageUri, View view) {
//						// holder.progressBar.setProgress(0);
//						// holder.progressBar.setVisibility(View.VISIBLE);
//					}
//
//					@Override
//					public void onLoadingFailed(String imageUri, View view,
//							FailReason failReason) {
//						// holder.progressBar.setVisibility(View.GONE);
//					}
//
//					@Override
//					public void onLoadingComplete(String imageUri,
//							View view, Bitmap loadedImage) {
//						// holder.progressBar.setVisibility(View.GONE);
//					}
//				}, new ImageLoadingProgressListener() {
//					@Override
//					public void onProgressUpdate(String imageUri,
//							View view, int current, int total) {
//						// holder.progressBar.setProgress(Math.round(100.0f
//						// * current / total));
//					}
//				});
//		
//		imageLoader.displayImage(Constants.baseUrl
//				+ val.get(position).getUser_pic(), holder.ProfileMine,
//				optionsProfile, new SimpleImageLoadingListener() {
//					@Override
//					public void onLoadingStarted(String imageUri, View view) {
//						// holder.progressBar.setProgress(0);
//						// holder.progressBar.setVisibility(View.VISIBLE);
//					}
//
//					@Override
//					public void onLoadingFailed(String imageUri, View view,
//							FailReason failReason) {
//						// holder.progressBar.setVisibility(View.GONE);
//					}
//
//					@Override
//					public void onLoadingComplete(String imageUri,
//							View view, Bitmap loadedImage) {
//						// holder.progressBar.setVisibility(View.GONE);
//					}
//				}, new ImageLoadingProgressListener() {
//					@Override
//					public void onProgressUpdate(String imageUri,
//							View view, int current, int total) {
//						// holder.progressBar.setProgress(Math.round(100.0f
//						// * current / total));
//					}
//				});
		// Check whether message is mine to show green background and align to
		// right
		if (!message.isLeft()) {
			holder.rlMine.setVisibility(View.VISIBLE);
			holder.rlOther.setVisibility(View.GONE);
		}
		// If not mine then it is from sender to show orange background and
		// align to left
		else {

			holder.rlMine.setVisibility(View.GONE);
			holder.rlOther.setVisibility(View.VISIBLE);

		}

		return convertView;
	}

	private static class ViewHolder {
		public TextView chattimeOther;
		public TextView chattimeMine;
		LinearLayout  rlOther;
		RelativeLayout rlMine;
		// ImageView ProfileMine,ProfileOther;
		TextView messageMine, messageOther;
		ImageView ProfileOther,ProfileMine;
	}

	@Override
	public long getItemId(int position) {
		// Unimplemented, because we aren't using Sqlite.
		return position;
	}

}
