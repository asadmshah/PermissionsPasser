package com.asadmshah.permissionspasser.sdk;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class ParcelableUtil {

    public static byte[] encode(Parcelable parcelable) {
        Parcel parcel = null;
        try {
            parcel = Parcel.obtain();
            parcelable.writeToParcel(parcel, 0);
            return parcel.marshall();
        } finally {
            if (parcel != null) parcel.recycle();
        }
    }

    public static byte[] encodeParcelableList(List<Parcelable> parcelableList) {
        Parcel parcel = null;
        try {
            parcel = Parcel.obtain();
            parcel.writeTypedList(parcelableList);
            return parcel.marshall();
        } finally {
            if (parcel != null) parcel.recycle();
        }
    }

    public static <T extends Parcelable> T decode(byte[] b, Parcelable.Creator<T> creator) {
        Parcel parcel = null;
        try {
            parcel = Parcel.obtain();
            parcel.unmarshall(b, 0, b.length);
            parcel.setDataPosition(0);
            return creator.createFromParcel(parcel);
        } finally {
            if (parcel != null) parcel.recycle();
        }
    }

    public static <T extends Parcelable> void decodeParcelableList(byte[] b, List<T> outList, Parcelable.Creator<T> creator) {
        Parcel parcel = null;
        try {
            parcel = Parcel.obtain();
            parcel.unmarshall(b, 0, b.length);
            parcel.setDataPosition(0);
            parcel.readTypedList(outList, creator);
        } finally {
            if (parcel != null) parcel.recycle();
        }
    }

}
