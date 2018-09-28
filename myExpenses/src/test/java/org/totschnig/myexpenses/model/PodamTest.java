package org.totschnig.myexpenses.model;

import android.content.SharedPreferences;

import junit.framework.TestCase;
import junit.framework.Assert;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import org.junit.Test;
import org.totschnig.myexpenses.model.Category;
import org.totschnig.myexpenses.model.Account;
import org.totschnig.myexpenses.model.Money;
import org.totschnig.myexpenses.model.Plan;

import android.content.Context;
import android.app.Application;

import org.totschnig.myexpenses.MyApplication;
import com.google.gson.*;
import android.content.SharedPreferences.*;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import org.robolectric.RuntimeEnvironment;

import java.util.Currency;

@RunWith(RobolectricTestRunner.class)
public class PodamTest extends TestCase {

    @Test
    public void testCategory(){

        SharedPreferences mPrefs = RuntimeEnvironment.application.getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);

        Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();

        PodamFactory podamFactory = new PodamFactoryImpl();
        Category newCategory = podamFactory.manufacturePojo(Category.class);

        Assert.assertNotNull(newCategory);

        String toJson = gson.toJson(newCategory);
        prefsEditor.putString("category", toJson);
        prefsEditor.commit();

        String serializable = mPrefs.getString("category", "");
        Category category = gson.fromJson(serializable, Category.class);

        assertEquals(newCategory.id, category.id);
        assertEquals(newCategory.getLabel(), category.getLabel());
        assertEquals(newCategory.parentId, category.parentId);

        System.out.println(newCategory.toString());
    }

    @Test
    public void testMoney(){

        SharedPreferences mPrefs = RuntimeEnvironment.application.getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);

        Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();

        PodamFactory podamFactory = new PodamFactoryImpl();
        Money newMoney = podamFactory.manufacturePojo(Money.class);

        Assert.assertNotNull(newMoney);

        String toJson = gson.toJson(newMoney);
        prefsEditor.putString("money", toJson);
        prefsEditor.commit();

        String serializable = mPrefs.getString("money", "");
        Money money = gson.fromJson(serializable, Money.class);

        assertEquals(newMoney.getCurrency(), money.getCurrency());
        assertEquals(newMoney.getAmountMinor(), money.getAmountMinor());
        assertEquals(newMoney.getFractionDigits(newMoney.getCurrency()), money.getFractionDigits(money.getCurrency()));

        System.out.println(newMoney.toString());
    }

    @Test
    public void testPlan(){

        SharedPreferences mPrefs = RuntimeEnvironment.application.getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);

        Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();

        PodamFactory podamFactory = new PodamFactoryImpl();
        Plan newPlan = podamFactory.manufacturePojo(Plan.class);

        Assert.assertNotNull(newPlan);

        String toJson = gson.toJson(newPlan);
        prefsEditor.putString("plan", toJson);
        prefsEditor.commit();

        String serializable = mPrefs.getString("plan", "");
        Plan plan = gson.fromJson(serializable, Plan.class);

        assertEquals(newPlan.dtstart, plan.dtstart);
        assertEquals(newPlan.rrule, plan.rrule);
        assertEquals(newPlan.title, plan.title);
        assertEquals(newPlan.description, plan.description);

        System.out.println(newPlan.toString());
    }



}
