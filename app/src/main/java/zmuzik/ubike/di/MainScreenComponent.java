package zmuzik.ubike.di;

import dagger.Component;
import zmuzik.ubike.MainActivity;
import zmuzik.ubike.MainScreenPresenter;


@ActivityScope
@Component(
        dependencies = AppComponent.class,
        modules = MainScreenModule.class
)
public interface MainScreenComponent {

    void inject(MainActivity mainActivity);

    //void inject(MainScreenPresenter presenter);

}