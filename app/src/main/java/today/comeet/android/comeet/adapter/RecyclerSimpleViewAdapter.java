package today.comeet.android.comeet.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import today.comeet.android.comeet.R;
import today.comeet.android.comeet.model.Event;

/**
 * Created by sachavent on 14/10/2016.
 */
public class RecyclerSimpleViewAdapter extends RecyclerView.Adapter<RecyclerSimpleViewAdapter.ViewHolder> {

    /**
     * List items
     */
    private List<Event> events;
    /**
     * the resource id of item Layout
     */
    private int eventLayout;

    /**
     * Constructor RecyclerSimpleViewAdapter
     * @param events : the list events
     * @param eventLayout : the resource id of eventView
     */
    public RecyclerSimpleViewAdapter(List<Event> events, int eventLayout) {
        this.events = events;
        this.eventLayout = eventLayout;
    }

    /**
     * Create View Holder by Type
     * @param parent, the view parent
     * @param viewType : the type of View
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // get inflater and get view by resource id itemLayout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        // return ViewHolder with View
        return new ViewHolder(v);
    }

    /**
     * Get the size of items in adapter
     * @return the size of items in adapter
     */
    @Override
    public int getItemCount() {
        return events.size();
    }


    /**
     * Bind View Holder with Items
     * @param holder: the view holder
     * @param position : the current position
     */
    @Override
    public void onBindViewHolder(RecyclerSimpleViewAdapter.ViewHolder holder, int position) {
        // find item by position
        Event event = events.get(position);
        // save information in holder, we have one type in this adapter
        holder.nameText.setText(event.getName());
        holder.dateText.setText(event.getDate());
    }

    /**
     *
     * Class viewHolder
     * Hold an textView
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView nameText;
        public TextView dateText;
        /**
         * Constructor ViewHolder
         * @param eventView: the itemView
         */
        public ViewHolder(View eventView) {
            super(eventView);
            nameText = (TextView) eventView.findViewById(R.id.event_name);
            dateText = (TextView) eventView.findViewById(R.id.event_date);
        }
    }
}
