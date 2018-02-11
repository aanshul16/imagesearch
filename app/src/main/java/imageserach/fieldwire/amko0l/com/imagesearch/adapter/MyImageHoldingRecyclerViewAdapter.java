package imageserach.fieldwire.amko0l.com.imagesearch.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import imageserach.fieldwire.amko0l.com.imagesearch.R;
import imageserach.fieldwire.amko0l.com.imagesearch.activities.ImageHolderFragment;

public class MyImageHoldingRecyclerViewAdapter extends RecyclerView.Adapter<MyImageHoldingRecyclerViewAdapter.ViewHolder> {

    private final List<String> mValues;
    private final ImageHolderFragment.OnImageHolderIInteractionListener mListener;
    private Context context;

    public MyImageHoldingRecyclerViewAdapter(List<String> items, ImageHolderFragment.OnImageHolderIInteractionListener listener
            , Context context) {
        mValues = items;
        mListener = listener;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Glide
                .with(context)
                .load(mValues.get(position))
                .listener(new RequestListener<Object, GlideDrawable>() {
                    @Override
                    public boolean onResourceReady(GlideDrawable resource, Object model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onException(Exception e, Object model, Target<GlideDrawable> target, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .error(R.drawable.ic_image_error)
                .centerCrop()
                .crossFade()
                .placeholder(R.drawable.placeholder)
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imageView;
        private ProgressBar progressBar;

        public ViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.item_image);
            progressBar = (ProgressBar) view.findViewById(R.id.grid_item_loading_indicator);
            view.setOnClickListener(this);
        }

        @Override
        public String toString() {
            return super.toString();
        }

        @Override
        public void onClick(View view) {
            mListener.onListImageHolderInteraction(mValues.get(getLayoutPosition()), imageView, progressBar);
        }
    }
}
