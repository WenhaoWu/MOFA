package com.example.yevgen.architectmuseo;

/**
 * Created by wenhaowu on 23/11/15.
 */
public class Solution {
    public int[] productExceptSelf(int[] nums) {
        int[] out = new int[nums.length];

        /*
        for (int k=0; k<out.length;k++){
            out[k]=1;
        }
        */

        for (int i=0; i<out.length;i++){
            int pTemp = 1;
            /*
            for (int j=0; j<nums.length; j++){
                if(j != i){
                    out[j] *= nums[i];
                }

            }
            */
            for (int j=0;j<nums.length;j++){
                if (j!=i){
                    pTemp *= nums[j];
                }
            }
            out[i]= pTemp;
        }
        return out;
    }
}