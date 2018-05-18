package com.appecco.learntowrite.dialog;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appecco.learntowrite.R;
import com.appecco.learntowrite.model.Rewards;
import com.appecco.utils.LoadedResources;

public class RewardFragment extends Fragment {
    private static final String REWARDS_PARAM = "rewardsParam";
    private static final String REWARD_TAG_PARAM = "rewardTagParam";

    private Rewards rewards;
    private String rewardTag;

    private OnRewardPurchaseListener rewardPurchaseListener;

    public RewardFragment() {
        // Required empty public constructor
    }

    public static RewardFragment newInstance (Rewards rewards, String rewardTag){
        RewardFragment fragment = new RewardFragment();
        Bundle args = new Bundle();
        args.putSerializable(REWARDS_PARAM, rewards);
        args.putSerializable(REWARD_TAG_PARAM, rewardTag);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            rewards = (Rewards)(getArguments().getSerializable(REWARDS_PARAM));
            rewardTag = getArguments().getString(REWARD_TAG_PARAM);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            rewardPurchaseListener = (OnRewardPurchaseListener) context;
        } catch (ClassCastException ex){
            throw new RuntimeException(context.toString() + " must implement OnRewardPurchaseListener");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int availableStars;
        int rewardCost;
        Drawable greenCheckDrawable = getResources().getDrawable(R.drawable.green_check);
        greenCheckDrawable.setBounds(0,0,40,40);

        View view = inflater.inflate(R.layout.fragment_reward, container, false);

        final Rewards.Reward reward = rewards.findByTag(rewardTag);

        Button rewardButton = (Button)view.findViewById(R.id.rewardButton);
        int resourceId = getResources().getIdentifier(reward.getResourceName(),"drawable", getContext().getPackageName());
        if (resourceId != 0) {
            rewardButton.setBackground(getResources().getDrawable(resourceId));
        }
        rewardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rewardPurchaseListener != null) {
                    LoadedResources.getInstance().playSound(R.raw.button_click);
                    rewardPurchaseListener.onRewardPurchase(reward);
                }
            }
        });

        rewardCost = reward.getCost();
        availableStars = rewards.getAvailableStars(getContext());

        TextView rewardCostText = (TextView)view.findViewById(R.id.rewardCostText);

        if (reward.isUnlocked()) {
            rewardCostText.setCompoundDrawables(greenCheckDrawable,null,null,null);
        } else {
            rewardCostText.setText(Integer.toString(rewardCost));
            if (rewardCost <= availableStars){
                rewardCostText.setTextColor(getResources().getColor(R.color.colorBlack));
            } else {
                rewardCostText.setTextColor(getResources().getColor(R.color.colorRed));
            }
        }

        ImageView rewardTypeIcon = (ImageView)view.findViewById(R.id.rewardTypeIcon);
        if ("texture".equals(reward.getType())){
            rewardTypeIcon.setImageResource(R.drawable.pencil);
        } else {
            rewardTypeIcon.setImageResource(R.drawable.landscape);
        }

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        rewardPurchaseListener = null;
    }

    public interface OnRewardPurchaseListener {
        void onRewardPurchase(Rewards.Reward reward);
    }
}
