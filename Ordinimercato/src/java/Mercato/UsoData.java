/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Mercato;

import java.util.*;

import java.io.Serializable;
import java.util.Date;
//import java.util.Calendar;

import java.text.SimpleDateFormat;

/**
 *
 * @author matteo grandi
 */
public class UsoData {
//protected String riga;

    public UsoData(){
        }

    protected String DatainStringa(){
        Calendar cal = Calendar.getInstance();
            int todayYear = cal.get(Calendar.YEAR);
            int todayMonth = cal.get(Calendar.MONTH)+1;
            int todayDay= cal.get(Calendar.DAY_OF_MONTH);
          
            return todayDay+"/"+todayMonth+"/"+todayYear;
                       // return d;

    }


    protected String DataRestituzione(){
        GregorianCalendar cal=new GregorianCalendar();
        int oggi = cal.get(Calendar.DAY_OF_WEEK);
        
        if((oggi==7)||(oggi==1)){
    cal.add(Calendar.DAY_OF_MONTH, 2);
}else
    cal.add(Calendar.DAY_OF_MONTH, 1);

      int todayYear = cal.get(Calendar.YEAR);
            int todayMonth = cal.get(Calendar.MONTH)+1;
            int todayDay= cal.get(Calendar.DAY_OF_MONTH);
            String d=todayDay+"/"+todayMonth+"/"+todayYear;
return d;
    }


    protected void StringainData(String data){
        SimpleDateFormat df = new SimpleDateFormat ("dd/MM/yyyy");
        Date d=new Date();


    }

protected int confrontaStringa(String dat){
    SimpleDateFormat df = new SimpleDateFormat ("dd/MM/yyyy");
    Date d=new Date();
    Date d1=new Date();

     try
        {
        d=df.parse(dat);
        }catch(Exception e) {

        }
        //String s=d+"  - -  "+d1;
 //if (dat.equalsIgnoreCase("14/7/2003"))

        if((d1.before(d))||(dat.equalsIgnoreCase("12/7/2003")))
            return 1;
        else return 0;
        
    
}


}
