import {Routes} from '@angular/router';
import {AddUserFormComponent} from './add-user-form/add-user-form.component';
import {AddAvailabilityComponent} from './add-availability-form/add-availability-form.component';
import {UserPageComponent} from './user-page/user-page.component';
import {AppComponent} from './app.component';
export const routes: Routes = [
  { path: 'add-user-form', component: AddUserFormComponent },
  { path: 'add-availability-form', component: AddAvailabilityComponent },
  { path: 'user-page/:id', component: UserPageComponent },
  { path: '', component: AppComponent }
];
