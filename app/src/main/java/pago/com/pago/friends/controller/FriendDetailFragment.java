package pago.com.pago.friends.controller;



import android.app.DialogFragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import pago.com.pago.R;
import pago.com.pago.friends.view.ConfirmationDialogueFragment;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class FriendDetailFragment extends Fragment implements View.OnClickListener, ConfirmationDialogueFragment.ConfirmationDialogueInterface {


    private static final String FIREND_NAME = "FIREND_NAME";
    private static final String FRIEND_ID = "FRIEND_AMOUNT";
    private static final String CONFIRMATION_DIALOGUE = "CONFIRMATION_DIALOGUE";
    private ConfirmationDialogueFragment confirmationDialogueFragment;

    public static FriendDetailFragment newInstance(String name, String payruID)
    {
        Bundle bundle = new Bundle();
        bundle.putString(FIREND_NAME,name);
        bundle.putString(FRIEND_ID,payruID);
        FriendDetailFragment friendDetailFragment = new FriendDetailFragment();
        friendDetailFragment.setArguments(bundle);
        return friendDetailFragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friend_detail, container, false);

        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Rupee_Foradian.ttf");


        //Typeface tf = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/Rupee_Foradian.ttf");
        ((TextView)view.findViewById(R.id.transfer_amount_text_view)).setTypeface(font);
        ((TextView)view.findViewById(R.id.transfer_amount_text_view)).setText("` 2400.00");
        ((TextView)view.findViewById(R.id.payru_id_text_view)).setText(getArguments().getString(FRIEND_ID));
        ((TextView)view.findViewById(R.id.name_text_view)).setText(getArguments().getString(FIREND_NAME));
        ((Button)view.findViewById(R.id.done_button)).setOnClickListener(this);
        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.done_button:
                if(confirmationDialogueFragment == null) {
                    confirmationDialogueFragment = new ConfirmationDialogueFragment();
                    confirmationDialogueFragment.setConfirmationDialogueInterface(this);
                    confirmationDialogueFragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);

                }
                confirmationDialogueFragment.show(getFragmentManager(),CONFIRMATION_DIALOGUE);
                break;
        }
    }

    @Override
    public void didPressDone() {
        confirmationDialogueFragment.dismiss();
        getActivity().onBackPressed();
    }

    @Override
    public void didPressCancel() {
        confirmationDialogueFragment.dismiss();
        getActivity().onBackPressed();

    }
}
