import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AppComponent } from './app.component';
import { AddUserFormComponent } from './add-user-form/add-user-form.component';
import { UserPageComponent } from './user-page/user-page.component';
import { AddAvailabilityComponent } from './add-availability-form/add-availability-form.component';

const routes: Routes = [];

@NgModule({
  imports: [
    RouterModule.forRoot(routes),
    AppComponent,
    AddUserFormComponent,
    UserPageComponent,
    AddAvailabilityComponent
  ]
})
export class AppModule { }
