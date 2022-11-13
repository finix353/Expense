package com.example.expense;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.StringBuilderPrinter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import org.w3c.dom.CDATASection;

import java.text.DateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link incomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class incomeFragment extends Fragment {

     //Firebase database

    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;

    //Recyclerview

    private RecyclerView recyclerView;

    //Text view

    private  TextView incomeTotalSum;

    //update edit text

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

    public incomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment incomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static incomeFragment newInstance(String param1, String param2) {
        incomeFragment fragment = new incomeFragment();
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
        View myview= inflater.inflate(R.layout.fragment_income, container, false);

        mAuth=FirebaseAuth.getInstance();

        FirebaseUser mUser=mAuth.getCurrentUser();
        String uid=mUser.getUid();


        mIncomeDatabase= FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);

        incomeTotalSum=myview.findViewById(R.id.income_txt_result);

        recyclerView=myview.findViewById(R.id.recycler_id_income);

        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int totalvalue = 0;

                for(DataSnapshot mysanapshot:snapshot.getChildren() ){

                    Data data=mysanapshot.getValue(Data.class);

                    totalvalue+=data.getAmount();

                    String stTotalvalue=String.valueOf(totalvalue);

                    incomeTotalSum.setText(stTotalvalue+".00" );


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

        FirebaseRecyclerAdapter<Data,MyVieHolder>adapter=new FirebaseRecyclerAdapter<Data, MyVieHolder>
                (
                       Data.class,
                       R.layout.income_recycler_data,
                       MyVieHolder.class,
                       mIncomeDatabase
                ) {
            @Override
            protected void populateViewHolder(MyVieHolder myVieHolder, Data data, int i) {

                myVieHolder.setType(data.getType());
                myVieHolder.setNote(data.getNote());
                myVieHolder.setDate(data.getDate());
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

    public static class MyVieHolder extends RecyclerView.ViewHolder{

        View mView;



        public MyVieHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
        }

        void setType(String type){
            TextView mType=mView.findViewById(R.id.type_txt_income);
            mType.setText(type);
        }

        void setNote(String note){
            TextView mNote=mView.findViewById(R.id.note_txt_income);
            mNote.setText(note);
        }

        void setDate(String date){
            TextView mDate=mView.findViewById(R.id.date_txt_income);
            mDate.setText(date);
        }

        void setAmount(int amount){

            TextView mAmount=mView.findViewById(R.id.amount_txt_income);

            String stamount= String.valueOf(amount);
            mAmount.setText(stamount);


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

          //set data to edit text..

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

                  String mdamount=String.valueOf(amount);

                  mdamount=edtAmount.getText().toString().trim();

                  int  myAmount=Integer.parseInt(mdamount);

                  String mDate= DateFormat.getDateInstance().format(new Date());

                  Data data=new Data(myAmount,type,note,post_key,mDate);
                  mIncomeDatabase.child(post_key).setValue(data);

                  dialog.dismiss();



              }
          });

          btnDelete.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {

                  mIncomeDatabase.child(post_key).removeValue();



                  dialog.dismiss();
              }
          });

          dialog.show();





      }



}