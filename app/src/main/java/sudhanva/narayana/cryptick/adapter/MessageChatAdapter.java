package sudhanva.narayana.cryptick.adapter;

import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;

import java.util.List;

import sudhanva.narayana.cryptick.ChatActivity;
import sudhanva.narayana.cryptick.R;
import sudhanva.narayana.cryptick.model.MessageChatModel;
import sudhanva.narayana.cryptick.utils.Constants;

public class MessageChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int SENDER = 0;
    private static final int RECIPIENT = 1;
    public Firebase removeTick;
    public Firebase updateBal;
    public Firebase tickBal;
    public int tickVal;
    private List<MessageChatModel> mListOfFireChat;

    public MessageChatAdapter(List<MessageChatModel> listOfFireChats) {
        mListOfFireChat = listOfFireChats;
    }

    @Override
    public int getItemViewType(int position) {
        if (mListOfFireChat.get(position).getRecipientOrSenderStatus() == SENDER) {
            Log.e("Adapter", " sender");
            return SENDER;
        } else {
            return RECIPIENT;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case SENDER:
                View viewSender = inflater.inflate(R.layout.sender_message, viewGroup, false);
                viewHolder = new ViewHolderSender(viewSender);
                break;
            case RECIPIENT:
                View viewRecipient = inflater.inflate(R.layout.recipient_message, viewGroup, false);
                viewHolder = new ViewHolderRecipient(viewRecipient);
                break;
            default:
                View viewSenderDefault = inflater.inflate(R.layout.sender_message, viewGroup, false);
                viewHolder = new ViewHolderSender(viewSenderDefault);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        switch (viewHolder.getItemViewType()) {
            case SENDER:
                ViewHolderSender viewHolderSender = (ViewHolderSender) viewHolder;
                configureSenderView(viewHolderSender, position);
                break;
            case RECIPIENT:
                ViewHolderRecipient viewHolderRecipient = (ViewHolderRecipient) viewHolder;
                configureRecipientView(viewHolderRecipient, position);
                break;
        }
    }

    private void configureSenderView(ViewHolderSender viewHolderSender, int position) {
        MessageChatModel senderFireMessage = mListOfFireChat.get(position);
        viewHolderSender.getSenderMessageTextView().setText(senderFireMessage.getMessage());
    }

    private void configureRecipientView(final ViewHolderRecipient viewHolderRecipient, final int position) {
        final MessageChatModel recipientFireMessage = mListOfFireChat.get(position);

        String recipient = recipientFireMessage.getRecipient();


        tickBal = new Firebase(Constants.FIREBASE_URL + "/" + Constants.CHILD_USERS + "/" + recipient);

        tickBal.child("tickBal").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tickVal = Integer.parseInt(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        if (recipientFireMessage.getTick() != null) {
            final int recipientTick = Integer.parseInt(String.valueOf(recipientFireMessage.getTick()));

            new CountDownTimer(recipientTick * 1000, 1000) {

                public void onTick(long millisUntilFinished) {
                    viewHolderRecipient.getRecipientMessageTextView().setText(String.valueOf((millisUntilFinished / 1000)));
                    viewHolderRecipient.getRecipientMessageTextView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (tickVal == 0) {
                                Log.i("Zero Inside", "Checked 0 successfully");
                                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(
                                        ChatActivity.getContext(), R.style.AppTheme
                                ));

                                // 2. Chain together various setter methods to set the dialog characteristics
                                builder.setMessage(R.string.buy_tick)
                                        .setTitle(R.string.no_tick);

                                // Add the buttons
                                builder.setPositiveButton(R.string.buy, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Log.i("onClick", "Buy button clicked");

                                    }
                                });
                                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // User cancelled the dialog
                                    }
                                });

                                // Create the AlertDialog
                                AlertDialog dialog = builder.create();             // Set other dialog properties

                                dialog.show();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(
                                        ChatActivity.getContext(), R.style.AppTheme
                                ));

                                // 2. Chain together various setter methods to set the dialog characteristics
                                builder.setMessage(R.string.tick_dialog_message + String.valueOf(tickVal))
                                        .setTitle(R.string.tick_dialog);

                                // Add the buttons
                                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        String recipient = recipientFireMessage.getRecipient();

                                        cancel();
                                        viewHolderRecipient.getRecipientMessageTextView().setText(recipientFireMessage.getMessage());

                                        String replace = recipientFireMessage.getTickURL();

                                        removeTick = new Firebase(replace.replace("%40", "@"));

                                        Log.i("FB Ref", removeTick.toString());
                                        Log.i("FB Ref Tick", removeTick.child("tick").toString());

                                        updateTick(removeTick, recipient);
                                        viewHolderRecipient.getRecipientMessageTextView().setOnClickListener(null);
                                    }
                                });
                                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // User cancelled the dialog
                                    }
                                });

                                // Create the AlertDialog
                                AlertDialog dialog = builder.create();                            // Set other dialog properties

                                dialog.show();
                            }

                        }
                    });
                }

                public void onFinish() {
                    viewHolderRecipient.getRecipientMessageTextView().setText(recipientFireMessage.getMessage());
                    Log.i("Get Tick URL", recipientFireMessage.getTickURL());

                    String replace = recipientFireMessage.getTickURL();

                    removeTick = new Firebase(replace.replace("%40", "@"));

                    Log.i("FB Ref", removeTick.toString());
                    Log.i("FB Ref Tick", removeTick.child("tick").toString());
                    removeTick.child("tick").setValue(null);
                    removeTick.child("tickURL").setValue(null);
                }
            }.start();

        } else
            viewHolderRecipient.getRecipientMessageTextView().setText(recipientFireMessage.getMessage());
    }

    @Override
    public int getItemCount() {
        return mListOfFireChat.size();
    }


    public void refillAdapter(MessageChatModel newFireChatMessage) {

        /*add new message chat to list*/
        mListOfFireChat.add(newFireChatMessage);

        /*refresh view*/
        notifyItemInserted(getItemCount() - 1);
    }

    public void refillFirsTimeAdapter(List<MessageChatModel> newFireChatMessage) {

        /*add new message chat to list*/
        mListOfFireChat.clear();
        mListOfFireChat.addAll(newFireChatMessage);
        /*refresh view*/
        notifyItemInserted(getItemCount() - 1);
    }

    public void cleanUp() {
        mListOfFireChat.clear();
    }


    /*==============ViewHolder===========*/

    /*ViewHolder for Sender*/

    private void updateTick(final Firebase removeTick, String recipient) {

        updateBal = new Firebase(Constants.FIREBASE_URL + "/" + Constants.CHILD_USERS + "/" + recipient + "/");

        updateBal.child("tickBal").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData currentData) {
                if (Integer.parseInt(currentData.getValue().toString()) == 0) {
                    Log.i("Zero Check", "Checked 0 successfully");

                } else {
                    currentData.setValue((Long) currentData.getValue() - 1);
                }
                return Transaction.success(currentData); //we can also abort by calling Transaction.abort()
            }

            @Override
            public void onComplete(FirebaseError firebaseError, boolean committed, DataSnapshot currentData) {
                //This method will be called once with the results of the transaction.
                removeTick.child("tick").setValue(null);
                removeTick.child("tickURL").setValue(null);
            }
        });
    }

    public class ViewHolderSender extends RecyclerView.ViewHolder {

        private TextView mSenderMessageTextView;

        public ViewHolderSender(View itemView) {
            super(itemView);
            mSenderMessageTextView = (TextView) itemView.findViewById(R.id.senderMessage);
        }

        public TextView getSenderMessageTextView() {
            return mSenderMessageTextView;
        }

        public void setSenderMessageTextView(TextView senderMessage) {
            mSenderMessageTextView = senderMessage;
        }
    }

    /*ViewHolder for Recipient*/
    public class ViewHolderRecipient extends RecyclerView.ViewHolder {

        private TextView mRecipientMessageTextView;

        public ViewHolderRecipient(View itemView) {
            super(itemView);
            mRecipientMessageTextView = (TextView) itemView.findViewById(R.id.recipientMessage);
        }

        public TextView getRecipientMessageTextView() {
            return mRecipientMessageTextView;
        }

        public void setRecipientMessageTextView(TextView recipientMessage) {
            mRecipientMessageTextView = recipientMessage;
        }
    }
}