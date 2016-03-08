package com.extenprise.mapp.medico.util;

import com.extenprise.mapp.medico.data.HasPhoto;

/**
 * Created by ambey on 8/3/16.
 */
public interface PhotoTaskCompleteListener {
    void taskCompleted(int action, HasPhoto entity);
}
