package com.pait.smartpos.interfaces;

import com.pait.smartpos.model.ProductClass;
import com.pait.smartpos.model.RateMasterClass;

public interface RecyclerViewToActivityInterface {

    public void onItemClick(ProductClass prod);
    public void onItemClick(RateMasterClass prod);
    public void calculation(float qty, float amnt);

}
