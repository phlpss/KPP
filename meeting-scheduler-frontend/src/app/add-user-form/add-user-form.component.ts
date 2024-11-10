import {Component} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {Router} from '@angular/router';

@Component({
  selector: 'add-user-form',
  standalone: true,
  imports: [
    FormsModule
  ],
  styleUrls: ['../app.component.scss'],
  templateUrl: './add-user-form.component.html'
})
export class AddUserFormComponent {
  newUser = {
    name: '',
    timezone: ''
  };
  timezones = ['UTC', 'UTC-5', 'UTC+1', 'UTC+8']; // Available time zones
  isModalOpen = false;

  constructor(private router: Router) {}

  openModal() {
    this.isModalOpen = true;
  }

  closeModal() {
    this.isModalOpen = false;
  }

  /**
   * This method validates the input (user’s name and timezone),
   * adds the new user to a mock local storage or service,
   * and then redirects back to the main page.
   * TODO: replace the local storage with a service.
   */
  addUser() {
    if (this.newUser.name && this.newUser.timezone) {
      // Simulating adding user to a service or store
      const userList = JSON.parse(localStorage.getItem('users') || '[]');
      const newUser = { id: userList.length + 1, ...this.newUser };
      userList.push(newUser);
      localStorage.setItem('users', JSON.stringify(userList));
      console.log('User added:', newUser);

      // Navigate to the main page after adding the user
      this.router.navigate(['/app-root']);
    } else {
      alert('Please fill out all fields');
    }
  }
}
