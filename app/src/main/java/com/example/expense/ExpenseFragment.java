package com.example.expense;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.expense.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExpenseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExpenseFragment extends Fragment {

    //Firebase database...
    private FirebaseAuth mAuth;
    private DatabaseReference mExpenseDatabase;

    //Recyclerview

    private RecyclerView recyclerView;

    private TextView expenseSumResult;

    //Edt data item;

    private EditText edtAmount;
    private EditText edtType;
    private EditText edtNote;

    //Button for update & delete

    private Button btnUpdate;
    private  Button btnDelete;

    //data item value

    private String type;
    private String  note;
    private int amount;

    private String post_key;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

   // public ExpenseFragment() {
        // Required empty public constructor


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExpenseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExpenseFragment newInstance(String param1, String param2) {
        ExpenseFragment fragment = new ExpenseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview= inflater.inflate(R.layout.fragment_expense, container, false);

        mAuth=FirebaseAuth.getInstance();

        FirebaseUser mUser=mAuth.getCurrentUser();
        String uid=mUser.getUid();

        mExpenseDatabase= FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(uid);

        expenseSumResult=myview.findViewById(R.id.expense_txt_result);

        recyclerView=myview.findViewById(R.id.recycler_id_expense);

        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int expenseSum=0;
                for(DataSnapshot mysanapshot:snapshot.getChildren() ) {

                    Data data = mysanapshot.getValue(Data.class);
                    expenseSum+=data.getAmount();
                    String strExpensesum=String.valueOf(expenseSum);

                    expenseSumResult.setText(strExpensesum+".00");


                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return myview;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Data,MyVieHolder> adapter=new FirebaseRecyclerAdapter<Data, MyVieHolder>
                (
                       Data.class,
                       R.layout.expense_recycler_data,
                       MyVieHolder.class,
                        mExpenseDatabase
                ) {
            @Override
            protected void populateViewHolder(MyVieHolder myVieHolder, Data data, int i) {

                myVieHolder.setDate(data.getDate());
                myVieHolder.setType(data.getType());
                myVieHolder.setNote(data.getNote());
                myVieHolder.setAmount(data.getAmount());

                myVieHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        post_key=getRef(i).getKey();

                        type=data.getType();
                        note=data.getNote();
                        amount=data.getAmount();



                         updateDataItem();
                    }
                });

            }
        };

        recyclerView.setAdapter(adapter);


    }
    public static class MyVieHolder extends RecyclerView.ViewHolder {

        View mView;


        public MyVieHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }
        private void setType(String type){
            TextView mType=mView.findViewById(R.id.type_txt_expense);
            mType.setText(type);
        }

        private void setNote(String note){
            TextView mNote=mView.findViewById(R.id.note_txt_expense);
            mNote.setText(note);
        }

        private void setDate(String date){
            TextView mDate=mView.findViewById(R.id.date_txt_expense);
            mDate.setText(date);
        }

        private  void setAmount(int amount){

            TextView mAmount=mView.findViewById(R.id.amount_txt_expense);

            String stramount= String.valueOf(amount);
            mAmount.setText(stramount);


        }







    }

     private void updateDataItem(){

         AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());
         LayoutInflater inflater=LayoutInflater.from(getActivity());
         View myview=inflater.inflate(R.layout.update_data_item,null);
         mydialog.setView(myview);

         edtAmount=myview.findViewById(R.id.amount_edt);
         edtType=myview.findViewById(R.id.type_edt);
         edtNote=myview.findViewById(R.id.note_edt);

         edtType.setText(type);
         edtType.setSelection(type.length());

         edtNote.setText(note);
         edtNote.setSelection(note.length());

         edtAmount.setText(String.valueOf(amount));
         edtAmount.setSelection(String.valueOf(amount).length());

         btnUpdate=myview.findViewById(R.id.btn_upd_update);
         btnDelete=myview.findViewById(R.id.btn_upd_Delete);

         AlertDialog dialog=mydialog.create();

         btnUpdate.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 type=edtType.getText().toString().trim();
                 note=edtNote.getText().toString().trim();

                 String stamount=String.valueOf(amount);

                 stamount=edtAmount.getText().toString().trim();


                 int  myAmount=Integer.parseInt(stamount);

                 String mDate= DateFormat.getDateInstance().format(new Date());

                 Data data=new Data(myAmount,type,note,post_key,mDate);
                 mExpenseDatabase.child(post_key).setValue(data);

                 dialog.dismiss();

             }
         });

         btnDelete.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 mExpenseDatabase.child(post_key).removeValue();

                 dialog.dismiss();

             }
         });

         dialog.show();

     }


}