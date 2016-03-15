package us.idinfor.smartcitizen.fragment;

import android.support.v4.app.Fragment;

import us.idinfor.smartcitizen.di.HasComponent;

public abstract class BaseFragment extends Fragment {

    @SuppressWarnings("unchecked")
    protected <C> C getComponent(Class<C> componentType) {
        return componentType.cast(((HasComponent<C>) getActivity()).getComponent());
    }
}