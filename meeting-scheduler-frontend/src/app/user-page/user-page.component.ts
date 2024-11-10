import {Component, OnInit} from '@angular/core';
import {NgForOf} from '@angular/common';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  selector: 'user-page',
  styleUrls: ['../app.component.scss'],
  templateUrl: './user-page.component.html',
  imports: [
    NgForOf
  ],
  standalone: true
})
export class UserPageComponent implements OnInit {
  userId!: number;

  user = {
    name: 'John Doe',
    timezone: 'UTC-5',
    availabilities: [
      {id: 1, day: 'Monday', start: '9:00', end: '12:00'},
      {id: 2, day: 'Wednesday', start: '14:00', end: '16:00'}
    ]
  };

  constructor(private route: ActivatedRoute, private router: Router) {
  }

  ngOnInit(): void {
    console.log('Opening user page...')
    this.userId = +this.route.snapshot.paramMap.get('id')!;
  }

  openAddAvailabilityForm() {
    console.log('Opening add availability form...');
  }

  deleteAvailability(availabilityId: number) {
    this.user.availabilities = this.user.availabilities.filter(a => a.id !== availabilityId);
    console.log('Deleted availability with ID:', availabilityId);
  }

  changeTimeZone() {
    console.log('Changing time zone for user:', this.user.name);
  }

  goBack() {
    this.router.navigate(['']);
  }
}
