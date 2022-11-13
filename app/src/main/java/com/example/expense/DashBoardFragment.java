package com.example.expense;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expense.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
 * Use the {@link DashBoardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashBoardFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //public DashBoardFragment() {
        // Required empty public constructor
    //Floating button

    private FloatingActionButton fab_main_btn;
    private FloatingActionButton fab_income_btn;
     private FloatingActionButton fab_expense_btn;

     //Floating button textview
    private TextView fab_income_txt;
    private TextView fab_expense_txt;

     // boolean
     private boolean isOpen=false;

     //Animation

    private Animation FadeOpen,FadeClose;

    //Dashboard income and expense result
    private TextView totalIncomeResult;
    private TextView totalExpenseResult;

    //Firebase ...

    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;

    //Recycler view
    private RecyclerView mRecyclerIncome;
    private RecyclerView mRecyclerExpense;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DashBoardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DashBoardFragment newInstance(String param1, String param2) {
        DashBoardFragment fragment = new DashBoardFragment();
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

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview = inflater.inflate(R.layout.fragment_dash_board, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(uid);

        mIncomeDatabase.keepSynced(true);
        mExpenseDatabase.keepSynced(true);

        //connect floating button to layout
        fab_main_btn = myview.findViewById(R.id.fb_main_plus_btn);
        fab_income_btn = myview.findViewById(R.id.income_Ft_btn);
        fab_expense_btn = myview.findViewById(R.id.expense_Ft_btn);

        //connect floating txt
        fab_income_txt = myview.findViewById(R.id.income_ft_text);
        fab_expense_txt = myview.findViewById(R.id.expense_ft_text);

        //total income and expense result set
        totalExpenseResult = myview.findViewById(R.id.expense_set_result);
        totalIncomeResult = myview.findViewById(R.id.income_set_result);

        //Recycler
        mRecyclerIncome=myview.findViewById(R.id.recycler_income);
        mRecyclerExpense=myview.findViewById(R.id.recycler_expense);


        //Animation Connect

        FadeOpen = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_open);
        FadeClose = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_close);


        fab_main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addData();

                if (isOpen) {
                    fab_income_btn.startAnimation(FadeClose);
                    fab_expense_btn.startAnimation(FadeClose);
                    fab_income_btn.setClickable(false);
                    fab_expense_btn.setClickable(false);

                    fab_income_txt.startAnimation(FadeClose);
                    fab_expense_txt.startAnimation(FadeClose);
                    fab_income_txt.setClickable(false);
                    fab_expense_txt.setClickable(false);
                    isOpen = false;

                } else {
                    fab_income_btn.startAnimation(FadeOpen);
                    fab_expense_btn.startAnimation(FadeOpen);
                    fab_income_btn.setClickable(true);
                    fab_expense_btn.setClickable(true);

                    fab_income_txt.startAnimation(FadeOpen);
                    fab_expense_txt.startAnimation(FadeOpen);
                    fab_income_txt.setClickable(true);
                    fab_expense_txt.setClickable(true);
                    isOpen = true;


                }


            }
        });

        //calculate total income..

        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int totalsum=0;

                for(DataSnapshot mysnap:snapshot.getChildren()) {

                    Data data = mysnap.getValue(Data.class);
                    totalsum += data.getAmount();

                     String stResult=String.valueOf(totalsum);
                     totalIncomeResult.setText(stResult+".00");



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Calculate total Expense...

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

              int totalsum=0;

                for(DataSnapshot mysnapshot:snapshot.getChildren()){

                    Data data=mysnapshot.getValue(Data.class);
                    totalsum+=data.getAmount();

                    String strTotalSum=String.valueOf(totalsum);
                    totalExpenseResult.setText(strTotalSum+".00");

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Recycler

        LinearLayoutManager layoutManagerIncome=new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);

        layoutManagerIncome.setStackFromEnd(true);
        layoutManagerIncome.setReverseLayout(true);
        mRecyclerIncome.setHasFixedSize(true);
        mRecyclerIncome.setLayoutManager(layoutManagerIncome);


        LinearLayoutManager layoutManagerExpense=new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        layoutManagerExpense.setReverseLayout(true);
        layoutManagerExpense.setStackFromEnd(true);
        mRecyclerExpense.setHasFixedSize(true);
        mRecyclerExpense.setLayoutManager(layoutManagerExpense);






        return myview;
    }

    //Floating Button Animation...

    private void ftAnimation(){

        if (isOpen) {
            fab_income_btn.startAnimation(FadeClose);
            fab_expense_btn.startAnimation(FadeClose);
            fab_income_btn.setClickable(false);
            fab_expense_btn.setClickable(false);

            fab_income_txt.startAnimation(FadeClose);
            fab_expense_txt.startAnimation(FadeClose);
            fab_income_txt.setClickable(false);
            fab_expense_txt.setClickable(false);
            isOpen = false;

        } else {
            fab_income_btn.startAnimation(FadeOpen);
            fab_expense_btn.startAnimation(FadeOpen);
            fab_income_btn.setClickable(true);
            fab_expense_btn.setClickable(true);

            fab_income_txt.startAnimation(FadeOpen);
            fab_expense_txt.startAnimation(FadeOpen);
            fab_income_txt.setClickable(true);
            fab_expense_txt.setClickable(true);
            isOpen = true;


        }


    }

    private void addData(){
       //fab button income....
        fab_income_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 incomeDataInsert();
            }
        });

        fab_expense_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                expenseDataInsert();
            }
        });

    }

    public void incomeDataInsert() {
            AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View myview = inflater.inflate(R.layout.custom_layout_for_insertdata, null);
            mydialog.setView(myview);
            AlertDialog dialog = mydialog.create();

            dialog.setCancelable(false);

            EditText edtAmount = myview.findViewById(R.id.amount_edt);
            EditText edtType = myview.findViewById(R.id.type_edt);
            EditText edtNote = myview.findViewById(R.id.note_edt);

            Button btnSave = myview.findViewById(R.id.btnSave);
            Button btnCansel = myview.findViewById(R.id.btnCancel);

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String type = edtType.getText().toString().trim();
                    String amount = edtAmount.getText().toString().trim();
                    String note = edtNote.getText().toString().trim();


                    if (TextUtils.isEmpty(type)) {
                        edtType.setError("Required Field..");
                        return;
                    }

                    if (TextUtils.isEmpty(amount)) {
                        edtAmount.setError("Required Field..");
                        return;
                    }

                    int ouramountint = Integer.parseInt(amount);

                    if (TextUtils.isEmpty(note)) {
                        edtNote.setError("Required Field..");
                        return;
                    }

                    String id = mIncomeDatabase.push().getKey();

                    String mDate = DateFormat.getDateInstance().format(new Date());
                    Data data = new Data(ouramountint, type, note, id, mDate);

                    mIncomeDatabase.child(id).setValue(data);
                    Toast.makeText(getActivity(), "Data Added", Toast.LENGTH_SHORT).show();
                    ftAnimation();
                    dialog.dismiss();


                }
            });

            btnCansel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ftAnimation();
                    dialog.dismiss();
                }
            });

            dialog.show();


        }

        public void expenseDataInsert() {
            AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View myview = inflater.inflate(R.layout.custom_layout_for_insertdata, null);
            mydialog.setView(myview);

            final AlertDialog dialog = mydialog.create();

            dialog.setCancelable(false);

            EditText amount = myview.findViewById(R.id.amount_edt);
            EditText type = myview.findViewById(R.id.type_edt);
            EditText note = myview.findViewById(R.id.note_edt);

            Button btnSave = myview.findViewById(R.id.btnSave);
            Button btnCansel = myview.findViewById(R.id.btnCancel);

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String tmAmount = amount.getText().toString().trim();
                    String tmtype = type.getText().toString().trim();
                    String tmnote = note.getText().toString().trim();

                    if (TextUtils.isEmpty(tmAmount)) {
                        amount.setError("Required Field.....");
                        return;
                    }

                    int inamount = Integer.parseInt(tmAmount);

                    if (TextUtils.isEmpty(tmtype)) {
                        type.setError("Required Field.....");
                        return;
                    }
                    if (TextUtils.isEmpty(tmnote)) {
                        note.setError("Required Field.....");
                        return;
                    }

                    String id = mExpenseDatabase.push().getKey();
                    String mDate = DateFormat.getDateInstance().format(new Date());

                    Data data = new Data(inamount, tmtype, tmnote, id, mDate);
                    mExpenseDatabase.child(id).setValue(data);

                    Toast.makeText(getActivity(), "Data added", Toast.LENGTH_SHORT).show();

                    ftAnimation();
                    dialog.dismiss();

                }
            });

            btnCansel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ftAnimation();
                    dialog.dismiss();
                }
            });
            dialog.show();


        }

        @Override
        public void onStart(){
        super.onStart();

            FirebaseRecyclerAdapter<Data,IncomeViewHolder>incomeAdapter=new FirebaseRecyclerAdapter<Data, IncomeViewHolder>
                    (
                       Data.class,
                        R.layout.dashboard_income,
                        DashBoardFragment.IncomeViewHolder.class,
                        mIncomeDatabase

                    ) {
                @Override
                protected void populateViewHolder(IncomeViewHolder incomeViewHolder, Data data, int i) {

                    incomeViewHolder.setIncomeType(data.getType());
                    incomeViewHolder.setIncomeAmount(data.getAmount());
                    incomeViewHolder.setIncomeDate(data.getDate());



                }
            };

              mRecyclerIncome.setAdapter(incomeAdapter);

              FirebaseRecyclerAdapter<Data,ExpenseViewHolder>expenseAdapter=new FirebaseRecyclerAdapter<Data, ExpenseViewHolder>
                      (
                         Data.class,
                              R.layout.dashboard_expense,
                              DashBoardFragment.ExpenseViewHolder.class,
                              mExpenseDatabase

                      ) {
                  @Override
                  protected void populateViewHolder(ExpenseViewHolder expenseViewHolder, Data data, int i) {
                          expenseViewHolder.setExpenseType(data.getType());
                          expenseViewHolder.setExpenseAmount(data.getAmount());
                          expenseViewHolder.setExpenseDate(data.getDate());


                  }
              };

              mRecyclerExpense.setAdapter(expenseAdapter);

        }

        //For income data
      public static class IncomeViewHolder extends  RecyclerView.ViewHolder{

      View mIncomeView;

      public IncomeViewHolder(View itemView){
            super(itemView);
         mIncomeView=itemView;
      }

      public void setIncomeType(String type){

          TextView mtype=mIncomeView.findViewById(R.id.type_Income_ds);
          mtype.setText(type);
      }
      public void setIncomeAmount(int amount){

          TextView mAmount=mIncomeView.findViewById(R.id.amount_income_ds);
          String strAmount=String.valueOf(amount);
          mAmount.setText(strAmount);
      }

      public void setIncomeDate(String date){

          TextView mDate=mIncomeView.findViewById(R.id.date_income_ds);
          mDate.setText(date);
      }



    }

    //For expense data

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder{

        View mExpenseView;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            mExpenseView=itemView;
        }
        public void setExpenseType(String type){

            TextView mtype=mExpenseView.findViewById(R.id.type_expense_ds);
            mtype.setText(type);
        }
        public void setExpenseAmount(int amount){

            TextView mAmount=mExpenseView.findViewById(R.id.amount_expense_ds);
            String strAmount=String.valueOf(amount);
            mAmount.setText(strAmount);
        }

        public void setExpenseDate(String date){

            TextView mDate=mExpenseView.findViewById(R.id.date_expense_ds);
            mDate.setText(date);
        }





    }








    }

