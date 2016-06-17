package es.us.hermes.smartcitizen.ui.fragment;

import android.support.v4.app.Fragment;

import es.us.hermes.smartcitizen.di.HasComponent;

public abstract class BaseFragment extends Fragment {

    @SuppressWarnings("unchecked")
    protected <C> C getComponent(Class<C> componentType){
        return componentType.cast(((HasComponent<C>) getActivity()).getComponent());
    }
}