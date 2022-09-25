interface ApiNewTask {
  title: string
  content: string
  deadline?: string
  budget: number
  roomId: string
}

interface ApiTask {
  id: string
  title: string
  content: string
  deadline?: string
  budget: number
  status: 'New' | 'Assigned' | 'Reviewing' | 'Closed'
  author: string
  authorMemberId: string
  assignee: string | null
  assigneeMemberId: string | null
  solutionNote?: string
}

interface ApiWallet {
  id: string
  coinsAmount: number
  memberId: string
}

interface ApiMember {
  id: string
  userId: string
  roomId: string
}

interface ApiRoom {
  id: string
  name: string
}

declare type UserRole = 'ADMIN' | 'USER'

interface ApiUser {
  id: string
  email: string
  name: string
  role: UserRole
}

interface Participant {
  id: string
  firstName: string
  lastName: string
}

interface ParticipantWithState extends Participant {
  isEnabled: boolean
}

interface ApiCourseEdition {
  courseId: string
  courseName: string
  participants: Participant[]
  lecturers: Participant[]
}

interface ApiNewRoom {
  name: string
  participants: Participant[]
  initialCoinsAmount: number
}

interface ApiValidationError {
  fieldName: string
  message: string
}
