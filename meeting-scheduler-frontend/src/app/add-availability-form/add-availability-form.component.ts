import {Component, Input} from '@angular/core';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-add-availability-form',
  standalone: true,
  imports: [
    FormsModule
  ],
  styleUrls: ['../app.component.scss'],
  templateUrl: './add-availability-form.component.html'
})
export class AddAvailabilityComponent {
  @Input() userId!: number;  // ID of the current user (passed from parent component)

  newAvailability = {
    day: '',
    start: '',
    end: ''
  };

  /**
   * This method checks if the availability data is complete and then adds it to the selected user’s schedule
   * (using mock data stored in local storage).
   * TODO: pass the current user’s ID from the parent component, so the availability gets assigned to the right user.
   */
  addAvailability() {
    if (this.newAvailability.day && this.newAvailability.start && this.newAvailability.end) {
      const users = JSON.parse(localStorage.getItem('users') || '[]');
      const user = users.find((u: any) => u.id === this.userId);
      if (user) {
        user.availabilities = user.availabilities || [];
        user.availabilities.push({ ...this.newAvailability, id: user.availabilities.length + 1 });
        localStorage.setItem('users', JSON.stringify(users));
        console.log('Availability added for user:', user);
      }
    } else {
      alert('Please fill out all fields');
    }
  }
}
