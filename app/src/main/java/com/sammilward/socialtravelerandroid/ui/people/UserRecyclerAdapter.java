package com.sammilward.socialtravelerandroid.ui.people;

        import android.content.Context;
        import android.content.Intent;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.TextView;

        import androidx.annotation.NonNull;
        import androidx.recyclerview.widget.RecyclerView;

        import com.sammilward.socialtravelerandroid.R;
        import com.sammilward.socialtravelerandroid.http.userManagement.User;
        import com.sammilward.socialtravelerandroid.ui.friends.UsersProfileActivity;

        import java.util.List;

public class UserRecyclerAdapter extends RecyclerView.Adapter<UserRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<User> users;
    private final LayoutInflater layoutInflater;

    public UserRecyclerAdapter(Context context, List<User> users)
    {
        this.context = context;
        this.users = users;
        this.layoutInflater = android.view.LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public UserRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.item_user_list, parent, false);
        return new ViewHolder(itemView);    }

    @Override
    public void onBindViewHolder(@NonNull UserRecyclerAdapter.ViewHolder holder, int position) {
        User user = users.get(position);
        holder.currentPosition = position;
        holder.lblName.setText(user.firstName + " " + user.lastName);
        holder.lblAge.setText(user.getDOBAsString("dd-MM-yyyy"));
        if (user.male) holder.lblGender.setText("Male");
        else holder.lblGender.setText("Female");
        holder.lblBirthCountry.setText(user.birthCountry);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView lblName, lblAge, lblGender, lblBirthCountry;
        public int currentPosition;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            lblName = itemView.findViewById(R.id.lblName);
            lblAge = itemView.findViewById(R.id.lblAge);
            lblGender = itemView.findViewById(R.id.lblGender);
            lblBirthCountry = itemView.findViewById(R.id.lblBirthCountry);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, UsersProfileActivity.class);
                    intent.putExtra("userId", users.get(currentPosition).id);
                    context.startActivity(intent);
                }
            });
        }
    }
}
