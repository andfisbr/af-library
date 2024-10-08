package br.com.afischer.aflibrary.extensions

import android.view.LayoutInflater
import android.view.View
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


inline fun <T: ViewBinding> ComponentActivity.viewBinding(
        crossinline bindingInflater: (LayoutInflater) -> T
) = lazy(LazyThreadSafetyMode.NONE) {
        bindingInflater.invoke(layoutInflater)
}








class FragmentViewBindingDelegate<VB: ViewBinding>(
        val fragment: Fragment,
        val viewBindingFactory: (View) -> VB
): ReadOnlyProperty<Fragment, VB> {

        private var binding: VB? = null

        init {
                fragment.lifecycle.addObserver(object: DefaultLifecycleObserver {
                        val viewLifecycleOwnerLiveDataObserver = Observer<LifecycleOwner?> {
                                it?.lifecycle?.addObserver(object: DefaultLifecycleObserver {
                                        override fun onDestroy(owner: LifecycleOwner) {
                                                binding = null
                                        }
                                })
                        }

                        override fun onCreate(owner: LifecycleOwner) {
                                fragment.viewLifecycleOwnerLiveData.observeForever(
                                        viewLifecycleOwnerLiveDataObserver
                                )
                        }

                        override fun onDestroy(owner: LifecycleOwner) {
                                fragment.viewLifecycleOwnerLiveData.removeObserver(
                                        viewLifecycleOwnerLiveDataObserver
                                )
                        }
                })
        }


        override fun getValue(thisRef: Fragment, property: KProperty<*>): VB {
                binding?.let { return it }

                val lifecycle = fragment.viewLifecycleOwner.lifecycle
                if (!lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
                        throw IllegalStateException("Should not attempt to get binding when Fragment views are destroyed.")
                }

                return viewBindingFactory(thisRef.requireView()).also { this.binding = it }
        }
}



fun <T: ViewBinding> Fragment.viewBinding(viewBindingFactory: (View) -> T) =
        FragmentViewBindingDelegate(this, viewBindingFactory)