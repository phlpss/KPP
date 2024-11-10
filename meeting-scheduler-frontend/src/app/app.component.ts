import { Component } from '@angular/core';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AddUserFormComponent } from './add-user-form/add-user-form.component';
import { NgForOf } from '@angular/common';
import {UserPageComponent} from './user-page/user-page.component';

@Component({
  imports: [RouterOutlet, RouterLink, FormsModule, AddUserFormComponent, NgForOf, UserPageComponent],
  selector: 'app-root',
  standalone: true,
  styleUrls: ['./app.component.scss'],
  templateUrl: './app.component.html'
})
export class AppComponent {
  users: any[] = [
    { id: 1, name: 'John Doe', timeZone: 'UTC', availability: [] },
    { id: 2, name: 'Jane Smith', timeZone: 'UTC+1', availability: [] }
  ];
  optimalMeetingHours = 'No optimal time calculated yet.';
  selectedTimeZone = 'UTC';
  timezones = ['UTC', 'UTC-5', 'UTC+1', 'UTC+8'];

  constructor(private router: Router) {  }

  goToUserPage(userId: number) {
    this.router.navigate([`user-page/${userId}`]); // Navigate to user page when clicked
  }
  openAddUserForm() {
    const addUserForm = document.getElementById('add-user-form');
    if (addUserForm) {
      addUserForm.style.display = 'block';
    }
  }

  closeAddUserForm() {
    const addUserForm = document.getElementById('add-user-form');
    if (addUserForm) {
      addUserForm.style.display = 'none';
    }
  }

  title = () => {

  }
}
