package pago.com.pago.friends.view;

import android.app.DialogFragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import pago.com.pago.R;

/**
 * Created by nIsHiTh on 10/9/2014.
 */
public class ConfirmationDialogueFragment extends DialogFragment implements View.OnClickListener {



    public interface ConfirmationDialogueInterface
    {
        public void didPressDone();
        public void didPressCancel();
    }

    private ConfirmationDialogueInterface confirmationDialogueInterface;

    public void setConfirmationDialogueInterface(ConfirmationDialogueInterface confirmationDialogueInterface) {
        this.confirmationDialogueInterface = confirmationDialogueInterface;
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.transfer_money_dialogue_fragment, container, false);
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Rupee_Foradian.ttf");
        ((EditText)view.findViewById(R.id.transfer_amount_text_view)).setTypeface(font);
        ((EditText)view.findViewById(R.id.transfer_amount_text_view)).setText("` 2400.00");
        view.findViewById(R.id.login_button).setOnClickListener(this);
        view.findViewById(R.id.register_button).setOnClickListener(this);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.login_button:
                if(this.confirmationDialogueInterface != null)
                {
                    this.confirmationDialogueInterface.didPressDone();
                }
                break;
            case R.id.register_button:
                if(this.confirmationDialogueInterface != null)
                {
                    this.confirmationDialogueInterface.didPressCancel();
                }
                break;
        }
    }
}
