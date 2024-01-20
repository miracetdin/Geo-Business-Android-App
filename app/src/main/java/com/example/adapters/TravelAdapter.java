package com.example.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.models.Travel;
import com.example.geo_business.R;

import java.util.ArrayList;

public class TravelAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public ArrayList<Travel> travelList;
    public Context travelContext;

    public static class TravelViewHolder extends RecyclerView.ViewHolder {

        //ImageView invoicePhoto;
        TextView id, username, travelDate, startLocation, endLocation, invoiceInfo, invoicePrice, priceEstimate, suspicious, status, approveByAccountant, approveDate;

        public TravelViewHolder(View view) {
            super(view);

            // Her duyuru; görsel, başlık ve tarih içermektedir
            //this.invoicePhoto = (ImageView) view.findViewById(R.id.travel_iv_photo);
            this.id = view.findViewById(R.id.travel_tv_id);
            this.username = view.findViewById(R.id.travel_tv_username);
            this.travelDate = view.findViewById(R.id.travel_tv_travelDate);
            this.startLocation = view.findViewById(R.id.travel_tv_startLocation);
            this.endLocation = view.findViewById(R.id.travel_tv_endLocation);
            //this.invoiceNote = view.findViewById(R.id.travel_tv_invoiceNote);
            this.invoiceInfo = view.findViewById(R.id.travel_tv_invoiceInfo);
            this.invoicePrice = view.findViewById(R.id.travel_tv_invoicePrice);
            //this.priceEstimate = view.findViewById(R.id.travel_tv_priceEstimate);
            //this.suspicious = view.findViewById(R.id.travel_tv_suspicious);
            this.status = view.findViewById(R.id.travel_tv_status);
            //this.approveByAccountant = view.findViewById(R.id.travel_tv_approveByAccountant);
            this.approveDate = view.findViewById(R.id.travel_tv_approveDate);

        }

    }

        public TravelAdapter(ArrayList<Travel> dataSet, Context context) {
            this.travelList = dataSet;
            this.travelContext = context;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.travel_card, parent, false);
            return new TravelViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Travel travel = travelList.get(travelList.size() - position - 1);

            if (travel != null) {
                // ImageView
                //((TravelViewHolder) holder).invoicePhoto.setImageResource(travel.getInvoicePhoto());

                /*
                // TextView'ler
                ((TravelViewHolder) holder).id.setText(String.valueOf(travel.getId()));
                ((TravelViewHolder) holder).username.setText(travel.getUsername());
                ((TravelViewHolder) holder).travelDate.setText(travel.getTravelDate());
                ((TravelViewHolder) holder).startLocation.setText(travel.getStartLocation());
                ((TravelViewHolder) holder).endLocation.setText(travel.getEndLocation());
                //((TravelViewHolder) holder).invoiceNote.setText(travel.getInvoiceNote());
                ((TravelViewHolder) holder).invoiceInfo.setText(travel.getInvoiceInfo());
                ((TravelViewHolder) holder).invoicePrice.setText(String.valueOf(travel.getInvoicePrice()));
                //((TravelViewHolder) holder).priceEstimate.setText(String.valueOf(travel.getPriceEstimate()));
                //((TravelViewHolder) holder).suspicious.setText(String.valueOf(travel.getSuspicious()));
                ((TravelViewHolder) holder).status.setText(travel.getStatus());
                //((TravelViewHolder) holder).approveByAccountant.setText(travel.getApproveByAccountant());
                ((TravelViewHolder) holder).approveDate.setText(travel.getApproveDate());

                 */

                ((TravelViewHolder) holder).id.setText("     ID:  " + String.valueOf(travel.getId()));
                ((TravelViewHolder) holder).username.setText("     Username:  " + travel.getUsername());
                ((TravelViewHolder) holder).travelDate.setText("     Date: " + travel.getTravelDate());
                ((TravelViewHolder) holder).startLocation.setText("     Start Location:  " + travel.getStartLocation());
                ((TravelViewHolder) holder).endLocation.setText("     End Location:  " + travel.getEndLocation());
                //((TravelViewHolder) holder).invoiceNote.setText("     Distance:  "travel.getInvoiceNote());
                ((TravelViewHolder) holder).invoiceInfo.setText("     Info:  " + travel.getInvoiceInfo());
                ((TravelViewHolder) holder).invoicePrice.setText("     Invoice Price:  " + String.valueOf(travel.getInvoicePrice()));
                //((TravelViewHolder) holder).priceEstimate.setText("     Estimated Price:  "String.valueOf(travel.getPriceEstimate()));
                //((TravelViewHolder) holder).suspicious.setText("     Suspicious:  "String.valueOf(travel.getSuspicious()));
                ((TravelViewHolder) holder).status.setText("     Status:  " + travel.getStatus());
                //((TravelViewHolder) holder).approveByAccountant.setText(travel.getApproveByAccountant());
                ((TravelViewHolder) holder).approveDate.setText("     Approve Date:  " + travel.getApproveDate());
            }
        }

    public void updateData(ArrayList<Travel> newTravelList) {
        // travelList'i güncelle
        travelList.clear();
        travelList.addAll(newTravelList);
        notifyDataSetChanged();
    }

        public int getItemCount() {
            return travelList.size();
        }
}